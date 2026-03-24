/*
 * Infomaniak Authenticator - Android
 * Copyright (C) 2026 Infomaniak Network SA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.infomaniak.auth.lib.internal

import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.AppStatus
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.lib.CredentialsForMigration
import com.infomaniak.auth.lib.NotConnectedAction
import com.infomaniak.auth.lib.internal.db.AccountEntity
import com.infomaniak.auth.lib.internal.db.AccountsDatabase
import com.infomaniak.auth.lib.internal.extensions.cancellable
import com.infomaniak.auth.lib.internal.extensions.toAccount
import com.infomaniak.auth.lib.internal.extensions.toEntity
import com.infomaniak.auth.lib.internal.managers.AuthenticatorManager
import com.infomaniak.auth.lib.internal.utils.DynamicLazyMap
import com.infomaniak.auth.lib.internal.utils.raceOf
import com.infomaniak.auth.lib.internal.utils.sharedFlow
import com.infomaniak.auth.lib.network.interfaces.TokenBridge
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transformLatest
import kotlin.time.Duration.Companion.seconds

internal class AuthenticatorFacadeImpl(
    private val db: AccountsDatabase,
    private val clientId: String,
    private val authenticatorManager: AuthenticatorManager,
    private val tokenBridge: TokenBridge,
    private val coroutineScope: CoroutineScope,
) : AuthenticatorFacade() {

    private val dao = db.getDao()

    private val accountEntities = flow {
        //TODO[ik-auth]: Ensure old accounts are inserted in the new db if needed.
        emitAll(dao.getAsFlow())
    }.shareIn(coroutineScope, SharingStarted.Eagerly, replay = 1)

    private val atLeastOneConnectedAccount: Flow<Boolean> = accountEntities.map { entities ->
        entities.any { entity -> entity.isLoggedIn }
    }.distinctUntilChanged().shareIn(coroutineScope, SharingStarted.WhileSubscribed(), replay = 1)

    private val accountsToLogin = DynamicLazyMap.sharedFlow(
        coroutineScope = coroutineScope,
        cacheManager = { _, _ ->
            delay(5.seconds) // Should be more than enough to keep the state between re-uses.
        }
    ) { userId: Long ->
        loginAttemptsFlow(userId)
    }

    private val proceedMigration: CompletableJob = Job()

    private val flowOfNull = flowOf(null)

    override val accounts: Flow<List<Account>> = channelFlow {
        accountEntities.collectLatest { entities ->
            val idsOfAccountsToLogIn = entities.mapNotNull { entity -> entity.id.takeUnless { entity.isLoggedIn } }.toSet()
            accountsToLogin.useElements(idsOfAccountsToLogIn) { map ->
                accountsFlow(entities, map).collectLatest { send(it) }
                awaitCancellation() // Stay in the useElements scope until a new list of accounts is received.
            }
        }
    }.flowOn(Dispatchers.Default).distinctUntilChanged().shareIn(coroutineScope, SharingStarted.Eagerly, replay = 1)

    override val appStatus: Flow<AppStatus> = appStatusFlow().shareIn(coroutineScope, SharingStarted.Eagerly, replay = 1)

    override suspend fun addAccounts(connectedAccounts: List<Account>) {
        val entities = connectedAccounts.map { it.toEntity(AccountEntity.Status.PasskeyRegistrationPending) }
        dao.upsert(entities)
    }

    override suspend fun removeAccount(token: String, id: Long) {
        authenticatorManager.removeAccount(token, id)
        dao.delete(id)
    }

    private fun accountsFlow(
        entities: List<AccountEntity>,
        accountsToLogin: Map<Long, Flow<NotConnectedAction?>>
    ): Flow<List<Account>> {
        val flows = entities.map { accountsToLogin[it.id] ?: flowOfNull }
        return combine(flows) { notConnectedActions ->
            entities.mapIndexed { index, entity -> entity.toAccount(notConnectedActions[index]) }
        }
    }

    private fun appStatusFlow(): Flow<AppStatus> = flow {
        var needsToShowOnboarding = false

        val appStatusFlow: Flow<AppStatus> = accountEntities.transformLatest { entities ->
            val atLeastOneConnectedAccount = entities.any { it.status == AccountEntity.Status.LoggedIn }
            val noConnectedAccount = !atLeastOneConnectedAccount

            if (noConnectedAccount) {
                needsToShowOnboarding = true
                if (entities.isEmpty()) {
                    emit(AppStatus.LoginRequired.NotMigrating)
                    /** Waiting for [addAccounts] to be called. */
                } else {
                    val needsMigration = entities.any { it.status == AccountEntity.Status.ToBeMigrated }
                    if (needsMigration) {
                        emit(AppStatus.LoginRequired.MigratingFromLegacyKAuth(proceed = proceedMigration::complete))
                        proceedMigration.join()
                    }
                    emit(AppStatus.LoggingIn(needsResolution = false))
                    emitAll(accounts.mapLatest { list ->
                        val somethingToResolve = list.any { (it.status as? Account.Status.NotConnected)?.action != null }
                        AppStatus.LoggingIn(needsResolution = somethingToResolve)
                    })
                }
            } else {
                if (needsToShowOnboarding) {
                    val proceedAsync: CompletableJob = Job()
                    emit(AppStatus.OnboardingDone(proceed = proceedAsync::complete))
                    proceedAsync.join()
                    needsToShowOnboarding = false
                }
                emit(AppStatus.SetupComplete)
            }
        }

        emitAll(appStatusFlow)
    }.distinctUntilChanged()

    private fun loginAttemptsFlow(userId: Long): Flow<NotConnectedAction?> = dao.get(userId).transformLatest { entity ->
        emit(null)
        when (entity?.status) {
            AccountEntity.Status.ToBeMigrated -> migrationAttempts(entity)
            AccountEntity.Status.PasskeyRegistrationPending, AccountEntity.Status.FirstPasskeyAuthenticationPending -> {
                registrationAttempts(entity)
            }
            AccountEntity.Status.LoggedIn, null -> Unit // Should not happen in practice.
        }
    }

    private suspend fun shouldTryImmediateLogin(): Boolean = raceOf(
        { proceedMigration.join(); true },
        { atLeastOneConnectedAccount.first(); false }
    ) || proceedMigration.isCompleted // In case it finished after a connected account was added.

    private suspend fun FlowCollector<NotConnectedAction?>.registrationAttempts(notRegisteredAccount: AccountEntity) {
        val passKeyAlreadyRegistered = when (val accountStatus = notRegisteredAccount.status) {
            AccountEntity.Status.PasskeyRegistrationPending -> false
            AccountEntity.Status.FirstPasskeyAuthenticationPending -> true
            else -> throw IllegalArgumentException("registrationAttempts doesn't support $accountStatus")
        }
        val userId = notRegisteredAccount.id
        val token = tokenBridge.getTokenFromDatabase(userId) ?: return
        withRetries {
            emit(null)
            if (!passKeyAlreadyRegistered) {
                authenticatorManager.registerPasskey(token, userId)
                dao.upsert(notRegisteredAccount.copy(status = AccountEntity.Status.FirstPasskeyAuthenticationPending))
            }
            val token = authenticatorManager.getToken(
                clientId = clientId,
                userId = userId,
            ).firstOrNull()!!
            tokenBridge.persistTokenForAccount(userId, token)
            dao.upsert(notRegisteredAccount.copy(status = AccountEntity.Status.LoggedIn))
        }
    }

    /**
     * Tries to migrate the given account from kAuth to Infomaniak Authenticator, with retries,
     * and emits the relevant [NotConnectedAction] as needed.
     *
     * 1. Tries to perform a login (3 different ways)
     * 2. Starts a migration session against the backend
     * 3. Registers a passkey
     * 4. Authenticate with it, getting a new access token
     */
    private suspend fun FlowCollector<NotConnectedAction?>.migrationAttempts(accountToMigrate: AccountEntity) {
        require(accountToMigrate.status == AccountEntity.Status.ToBeMigrated)

        if (shouldTryImmediateLogin()) {
            tryCrossAppLogin(accountToMigrate) { return }
            tryToMigrateViaOngoingLogin(accountToMigrate) { return }
        }
        withRetries {
            val credentialsAsync = CompletableDeferred<CredentialsForMigration>()
            val reLogin = NotConnectedAction.ReLogin(
                legacyAccount = accountToMigrate.toAccount(null),
                sendCredentials = credentialsAsync::complete,
            )
            emit(reLogin)
            credentialsAsync.await()
            emit(null)

            attemptMigration(accountToMigrate, TODO("Try to login with credentials and OTP"))
        }

    }

    private suspend inline fun FlowCollector<NotConnectedAction?>.tryCrossAppLogin(
        notConnectedAccount: AccountEntity,
        onLoginSuccess: () -> Nothing
    ) {
        val userId = notConnectedAccount.id
        withRetries(onGiveUp = { return }) {
            emit(null)
            val temporaryToken = tokenBridge.getTokenFromCrossAppLogin(userId) ?: return
            attemptMigration(notConnectedAccount, temporaryToken)
            onLoginSuccess()
        }
    }

    private suspend inline fun FlowCollector<NotConnectedAction?>.tryToMigrateViaOngoingLogin(
        notConnectedAccount: AccountEntity,
        onLoginSuccess: () -> Nothing
    ) {
        val userId = notConnectedAccount.id
        withRetries(onGiveUp = { return }) {
            emit(null)
            attemptMigration(notConnectedAccount, TODO("Try to jump into an ongoing login"))
            onLoginSuccess()
        }
    }

    private suspend fun attemptMigration(notConnectedAccount: AccountEntity, temporaryToken: String) {
        val userId = notConnectedAccount.id
        TODO("Perform the migration and passkey registration steps")
        // webAuthnRepository.getMigrationOptions()
        // authenticatorManager.registerPasskey()
        val tokenFromPasskeyAuth = authenticatorManager.getToken(
            clientId = clientId,
            userId = userId,
        ).firstOrNull()!!
        tokenBridge.persistTokenForAccount(userId, tokenFromPasskeyAuth)
        dao.upsert(notConnectedAccount.copy(status = AccountEntity.Status.LoggedIn))
    }

    private suspend inline fun <R> FlowCollector<NotConnectedAction?>.withRetries(
        onGiveUp: () -> Unit = {},
        block: () -> R
    ): R {
        while (true) {
            runCatching {
                return block()
            }.cancellable().onFailure {
                //TODO[ik-Auth]: Report the issue
                if (it is NullPointerException) { // Local errors, no recourse.
                    emit(NotConnectedAction.Issue.NonRetriable("Ooops…"))
                    awaitCancellation()
                }
                val shouldRetryAsync = CompletableDeferred<Boolean>()
                val issue = NotConnectedAction.Issue.Retriable(shouldRetryAsync::complete)
                emit(issue)
                val shouldRetry = shouldRetryAsync.await()
                if (!shouldRetry) onGiveUp()
            }
        }
    }
}
