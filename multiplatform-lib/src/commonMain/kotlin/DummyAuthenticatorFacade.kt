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
package com.infomaniak.auth.lib

import com.infomaniak.auth.lib.internal.db.AccountEntity
import com.infomaniak.auth.lib.internal.extensions.toAccount
import com.infomaniak.auth.lib.internal.extensions.toEntity
import com.infomaniak.auth.lib.internal.managers.AuthenticatorManager
import com.infomaniak.auth.lib.internal.repositories.AccountsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlin.time.Duration

class DummyAuthenticatorFacade internal constructor(
    private val accountsRepository: AccountsRepository,
    private val authenticatorManager: AuthenticatorManager,
    scope: CoroutineScope,
    loadingDuration: Duration,
    resetAfter: Duration,
) : AuthenticatorFacade() {
    override val accounts: Flow<List<Account>>

    private var _accounts: List<Account> by MutableStateFlow<List<Account>>(emptyList()).also {
        accounts = accountsRepository.getAccounts().map {
            it.map { it.toAccount(action = null) }
        }
    }::value

    private val next = Channel<Unit>()

    override val appStatus: Flow<AppStatus> = flow {
        var i = 0
        var isMigratingFromLegacyKAuth = true
        while (true) {
            val loginRequiredStatus: AppStatus.LoginRequired = when {
                isMigratingFromLegacyKAuth -> AppStatus.LoginRequired.MigratingFromLegacyKAuth(proceed = { next.trySend(Unit) })
                else -> AppStatus.LoginRequired.NotMigrating
            }
            emit(loginRequiredStatus)
            next.receive() // Waits for the addAccounts function or the proceed lambda to be called.
            emit(AppStatus.LoggingIn(needsResolution = false))
            delay(loadingDuration)
            if (isMigratingFromLegacyKAuth) {
                isMigratingFromLegacyKAuth = false
                val legacyAccount = Account(
                    id = 0,
                    fullName = "John",
                    initials = "Smith",
                    email = "john.smith@example.com",
                    avatarUrl = "https://avatars.githubusercontent.com/u/1788629?v=4",
                    status = Account.Status.NotConnected(null)
                )
                _accounts += legacyAccount.copy(
                    status = Account.Status.NotConnected(
                        action = NotConnectedAction.ReLogin(
                            legacyAccount = legacyAccount,
                            sendCredentials = { next.trySend(Unit) }
                        )
                    )
                )
                emit(AppStatus.LoggingIn(needsResolution = true))
                next.receive()
            }
            emit(AppStatus.OnboardingDone(proceed = { next.trySend(Unit) }))
            next.receive()
            emit(AppStatus.SetupComplete)
            delay(resetAfter)
            i++
        }
    }.distinctUntilChanged().shareIn(scope, SharingStarted.Lazily, replay = 1)

    override suspend fun addAccounts(connectedAccounts: List<Account>) {
        _accounts += connectedAccounts
        if (connectedAccounts.isNotEmpty()) next.trySend(Unit)
        val accountWithNoError = connectedAccounts.first()
        val accountInError = accountWithNoError.copy(id = 123).toEntity(AccountEntity.Status.PasskeyRegistrationPending)
        accountsRepository.upsertAccounts(connectedAccounts.map { it.toEntity(AccountEntity.Status.LoggedIn) })
        accountsRepository.upsertAccounts(listOf(accountInError))
    }

    override suspend fun removeAccount(token: String, id: Long) {
        authenticatorManager.removeAccount(token, id)
        accountsRepository.deleteAccount(id)
    }
}
