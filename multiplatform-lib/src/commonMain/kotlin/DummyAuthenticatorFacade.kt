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

import com.infomaniak.auth.lib.repository.AccountsRepository
import com.infomaniak.auth.lib.room.accounts.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlin.time.Duration

class DummyAuthenticatorFacade(
	accountsRepository: AccountsRepository,
    scope: CoroutineScope,
    loadingDuration: Duration,
    resetAfter: Duration,
) : AuthenticatorFacade() {
    override val accounts: Flow<List<Account>>

    private var _accounts: List<Account> by MutableStateFlow<List<Account>>(emptyList()).also {
        accounts = accountsRepository.getAccounts()
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
            emit(AppStatus.LoggingIn(null))
            delay(loadingDuration)
            val pendingAction: NotConnectedAction? = when (i % 3) {
                0 -> null
                1 -> NotConnectedAction.ReLogin(
                    legacyAccount = Account(
                        id = 0,
                        fullName = "John",
                        initials = "Smith",
                        email = "john.smith@example.com",
                        avatarUrl = "https://picsum.photos/id/3/200/200",
                        status = Account.Status.NotConnected(null)
                    ),
                    sendCredentials = { next.trySend(Unit) })
                else -> NotConnectedAction.Issue.Retriable(proceed = { next.trySend(Unit) })
            }
            emit(AppStatus.LoggingIn(pendingAction))
            if (pendingAction != null) next.receive()
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
    }
}
