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
@file:OptIn(ExperimentalCoroutinesApi::class)

package com.infomaniak.auth.lib.internal

import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.Account.Status.NotConnected.ReLogin
import com.infomaniak.auth.lib.AppStatus
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.lib.CredentialsForMigration
import com.infomaniak.auth.lib.Issue
import com.infomaniak.auth.lib.Issue.Retriable.Reason
import com.infomaniak.auth.lib.internal.db.AccountEntity
import com.infomaniak.auth.lib.internal.db.AccountsDatabase
import com.infomaniak.auth.lib.internal.extensions.cancellable
import com.infomaniak.auth.lib.internal.extensions.firstOrElse
import com.infomaniak.auth.lib.internal.extensions.toAccount
import com.infomaniak.auth.lib.internal.extensions.toEntity
import com.infomaniak.auth.lib.internal.managers.AuthenticatorManager
import com.infomaniak.auth.lib.internal.managers.MigrationManager
import com.infomaniak.auth.lib.internal.utils.DynamicLazyMap
import com.infomaniak.auth.lib.internal.utils.raceOf
import com.infomaniak.auth.lib.internal.utils.sharedFlow
import com.infomaniak.auth.lib.internal.utils.waitForComplete
import com.infomaniak.auth.lib.internal.utils.withTimeoutOrNull
import com.infomaniak.auth.lib.network.interfaces.AuthenticatorBridge
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
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
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.io.IOException
import network.exceptions.ApiException
import kotlin.time.Duration.Companion.seconds

internal class AuthenticatorFacadeImpl(
    accountsDatabase: AccountsDatabase,
    private val clientId: String,
    private val authenticatorManager: AuthenticatorManager,
    private val migrationManager: MigrationManager,
    private val authenticatorBridge: AuthenticatorBridge,
    private val crashReport: CrashReportInterface,
    private val coroutineScope: CoroutineScope,
) : AuthenticatorFacade() {

    private val dao = accountsDatabase.getDao()

    private val accountEntities = flow {
        migrationManager.addLegacyAccountsToDB()
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

    override val appStatus: SharedFlow<AppStatus> = appStatusFlow()
        .shareIn(coroutineScope, SharingStarted.Eagerly, replay = 1)

    override suspend fun addAccounts(connectedAccounts: List<Account>) {
        val entities = connectedAccounts.map { it.toEntity(AccountEntity.Status.PasskeyRegistrationPending) }
        dao.upsert(entities)
    }

    override suspend fun removeAccount(token: String, id: Long) {
        authenticatorManager.removeAccount(token, id)
        dao.delete(id)
    }

    override suspend fun refreshTokenFor(userId: Long) {
        val token = authenticatorManager.getToken(clientId, userId).firstOrElse {
            error("Could not get the key for user $userId from the storage: $it")
        }
        authenticatorBridge.persistTokenForAccount(userId, token)
    }

    private fun accountsFlow(
        entities: List<AccountEntity>,
        accountsToLogin: Map<Long, Flow<Account.Status.NotConnected?>>
    ): Flow<List<Account>> {
        val flows = entities.map { accountsToLogin[it.id] ?: flowOfNull }
        return combine(flows) { notConnectedActions ->
            entities.mapIndexed { index, entity -> entity.toAccount(notConnectedActions[index]) }
        }
    }

    private fun appStatusFlow(): Flow<AppStatus> = flow {
        var needsToShowEverythingReady = false

        val appStatusFlow: Flow<AppStatus> = accountEntities.transformLatest { entities ->
            val atLeastOneConnectedAccount = entities.any { it.status == AccountEntity.Status.LoggedIn }
            val noConnectedAccount = !atLeastOneConnectedAccount

            if (noConnectedAccount) {
                needsToShowEverythingReady = true
                if (entities.isEmpty()) {
                    emit(AppStatus.LoginRequired.NotMigrating)
                    /** Waiting for [addAccounts] to be called, which will cancel this, as `accountEntities` emits. */
                    awaitCancellation()
                } else {
                    val needsMigration = entities.any { it.status == AccountEntity.Status.ToBeMigrated }
                    if (needsMigration) {
                        emit(AppStatus.LoginRequired.MigratingFromLegacyKAuth(proceed = proceedMigration::complete))
                        proceedMigration.join()
                    }
                    emit(AppStatus.LoggingIn)
                    /** Continue towards [AppStatus.SetupComplete] once all accounts are waiting for an action (no loading). */
                    val accountToReloginOrSkip: Account? = accounts.transform { list ->
                        if (list.size == 1 && list.single().status is ReLogin) {
                            return@transform emit(list.single())
                        }
                        val stillTryingToConnect = list.any { account ->
                            account.status is Account.Status.NotConnected.AttemptingToConnect
                        }
                        if (!stillTryingToConnect) emit(null) // Emit to skip
                    }.first()
                    accountToReloginOrSkip?.let { accountToRelogin ->
                        val skipAsync: CompletableJob = Job()
                        emit(AppStatus.LoginRequired.MustReLogin(accountToRelogin.id, skipAsync::complete))
                        raceOf(
                            { skipAsync.join() },
                            {
                                accounts.first { list ->
                                    val account = list.singleOrNull() ?: return@first false
                                    account.status is Account.Status.LoggedIn
                                }
                            }
                        )
                    }
                }
            } else if (needsToShowEverythingReady) {
                waitForComplete { proceedAsync ->
                    emit(AppStatus.EverythingReady(proceed = proceedAsync::complete))
                }
            }
            while (true) {
                needsToShowEverythingReady = false
                waitForComplete { addAnAccountAsync ->
                    emit(AppStatus.SetupComplete(addAnAccount = addAnAccountAsync::complete))
                }
                needsToShowEverythingReady = true
                waitForComplete { backAsync ->
                    emit(AppStatus.AddingAnAccount(cancel = backAsync::complete))
                }
            }
        }

        emitAll(appStatusFlow)
    }.distinctUntilChanged()

    private fun loginAttemptsFlow(userId: Long): Flow<Account.Status.NotConnected?> = dao.get(userId).transformLatest { entity ->
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
        {
            proceedMigration.join()
            true
        },
        {
            atLeastOneConnectedAccount.first { it }
            false
        },
    ) || proceedMigration.isCompleted // In case it finished after a connected account was added.

    private suspend fun FlowCollector<Account.Status.NotConnected>.registrationAttempts(notRegisteredAccount: AccountEntity) {
        val passKeyAlreadyRegistered = when (val accountStatus = notRegisteredAccount.status) {
            AccountEntity.Status.PasskeyRegistrationPending -> false
            AccountEntity.Status.FirstPasskeyAuthenticationPending -> true
            else -> throw IllegalArgumentException("registrationAttempts doesn't support $accountStatus")
        }
        val userId = notRegisteredAccount.id
        val token = authenticatorBridge.getTokenFromDatabase(userId) ?: return
        withRetries(userId = userId) {
            emit(Account.Status.NotConnected.AttemptingToConnect)
            if (!passKeyAlreadyRegistered) {
                // TODO do that only if we don't need to use the backed up files
                authenticatorManager.deleteKeysFor(notRegisteredAccount.id)
                authenticatorManager.registerPasskey(token, userId)
                dao.upsert(notRegisteredAccount.copy(status = AccountEntity.Status.FirstPasskeyAuthenticationPending))
            }
            val token = authenticatorManager.getToken(
                clientId = clientId,
                userId = userId,
            ).firstOrElse { error("Key not found: ${it.details}") }
            authenticatorBridge.persistTokenForAccount(userId, token)
            dao.upsert(notRegisteredAccount.copy(status = AccountEntity.Status.LoggedIn))
        }
    }

    /**
     * Tries to migrate the given account from kAuth to Infomaniak Authenticator, with retries,
     * and emits the relevant [Account.Status.NotConnected] as needed.
     *
     * 1. Tries to perform a login (3 different ways)
     * 2. Starts a migration session against the backend
     * 3. Registers a passkey
     * 4. Authenticate with it, getting a new access token
     */
    private suspend fun FlowCollector<Account.Status.NotConnected>.migrationAttempts(accountToMigrate: AccountEntity) {
        require(accountToMigrate.status == AccountEntity.Status.ToBeMigrated)

        if (shouldTryImmediateLogin()) {
            tryCrossAppLogin(accountToMigrate) { return }
            tryToMigrateViaOngoingLogin(accountToMigrate) { return }
        }
        tryMigratingWithReLogin(accountToMigrate)
    }

    private suspend inline fun FlowCollector<Account.Status.NotConnected>.tryCrossAppLogin(
        notConnectedAccount: AccountEntity,
        onLoginSuccess: () -> Nothing
    ) {
        val userId = notConnectedAccount.id
        withRetries(userId, onGiveUp = { return }) {
            emit(Account.Status.NotConnected.AttemptingToConnect)
            val apiToken = withTimeoutOrNull(
                waitForTimeout = {
                    delay(8.seconds)
                    "getTokenFromCrossAppLogin timed out"
                },
                onTimeout = { message -> crashReport.capture(userId, message) }
            ) {
                authenticatorBridge.getTokenFromCrossAppLogin(userId)
            } ?: return
            val authentication = MigrationAuthentication.CrossAppLogin(apiToken)
            if (attemptMigration(notConnectedAccount, authentication)) onLoginSuccess() else return
        }
    }

    private suspend inline fun FlowCollector<Account.Status.NotConnected>.tryToMigrateViaOngoingLogin(
        notConnectedAccount: AccountEntity,
        onLoginSuccess: () -> Nothing
    ) {
        withRetries(notConnectedAccount.id, onGiveUp = { return }) {
            emit(Account.Status.NotConnected.AttemptingToConnect)
            val authentication = MigrationAuthentication.OngoingLogin
            if (attemptMigration(notConnectedAccount, authentication)) onLoginSuccess() else return
        }
    }

    private suspend fun attemptMigration(
        notConnectedAccount: AccountEntity,
        authentication: MigrationAuthentication,
    ): Boolean {
        val userId = notConnectedAccount.id
        val succeeded = migrationManager.tryMigrating(
            userId = userId,
            authentication = authentication,
            persistUser = { apiToken ->
                val userProfile = authenticatorManager.getUserProfile(apiToken.accessToken)
                userProfile.apiToken = apiToken
                authenticatorBridge.persistUserProfile(userProfile)
            },
        )

        if (succeeded.not()) return false

        dao.upsert(notConnectedAccount.copy(status = AccountEntity.Status.LoggedIn))

        return true
    }

    private suspend fun FlowCollector<ReLogin>.tryMigratingWithReLogin(accountToMigrate: AccountEntity) {
        var status = ReLogin(
            legacyAccount = accountToMigrate.toAccount(null),
            hadIncorrectPassword = false,
            lastIssue = null,
            sendCredentials = null
        )
        loop@ while (true) {
            status = runCatching {
                val credentialsAsync = CompletableDeferred<CredentialsForMigration>()
                status = status.copy(sendCredentials = credentialsAsync::complete)
                emit(status)
                val credentialsForMigration = credentialsAsync.await()
                status = status.copy(hadIncorrectPassword = false, lastIssue = null, sendCredentials = null)
                emit(status)
                val authentication = MigrationAuthentication.NoOngoingLogin(credentialsForMigration.password)
                val succeeded = attemptMigration(accountToMigrate, authentication)
                when {
                    succeeded -> return
                    else -> status.copy(hadIncorrectPassword = true)
                }
            }.cancellable().getOrElse {
                it.printStackTrace()
                status.copy(lastIssue = it.toIssueReason(accountToMigrate.id))
            }
        }
    }


    private suspend inline fun <R> FlowCollector<Account.Status.NotConnected>.withRetries(
        userId: Long,
        onGiveUp: () -> Unit = {},
        block: () -> R
    ): R {
        while (true) {
            runCatching {
                return block()
            }.cancellable().onFailure {
                it.printStackTrace()
                if (it is IllegalStateException || it is IllegalArgumentException) { // Local errors, no recourse.
                    val issue = Issue.NonRetriable(it.message ?: it::class.simpleName ?: "$it")
                    emit(Account.Status.NotConnected.LoginFailed(issue))
                    awaitCancellation()
                }
                val issueReason = it.toIssueReason(userId)
                val shouldRetryAsync = CompletableDeferred<Boolean>()
                val issue = Issue.Retriable(reason = issueReason, proceed = shouldRetryAsync::complete)
                emit(Account.Status.NotConnected.LoginFailed(issue))
                val shouldRetry = shouldRetryAsync.await()
                if (shouldRetry) continue else onGiveUp()
            }
        }
    }

    private fun Throwable.toIssueReason(userId: Long): Reason = when (this) {
        is IOException -> Reason.NetworkIssue
        is ApiException if (statusCode == 503) -> Reason.ServerUnavailable
        is ApiException.ApiErrorException -> {
            crashReport.capture(userId, "re-login migration attempt failed", this)
            Reason.Other(12_000 + statusCode, "http $statusCode $errorCode $errorMessage")
        }
        is ApiException.UnexpectedApiErrorFormatException -> {
            crashReport.capture(userId, "re-login migration attempt failed", this)
            Reason.Other(22_000 + statusCode, "http $statusCode $bodyResponse")
        }
        else -> {
            crashReport.capture(userId, "re-login migration attempt failed", this)
            Reason.Other(11_000, message ?: this::class.simpleName ?: "$this")
        }
    }
}
