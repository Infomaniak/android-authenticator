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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

abstract class AuthenticatorFacade internal constructor() {
    companion object {

        fun dummyInstance(
            scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
			accountsRepository: AccountsRepository,
            loadingDurationMillis: Long = 2.seconds.inWholeMilliseconds,
            resetAfterMillis: Long = 20.seconds.inWholeMilliseconds,
        ): AuthenticatorFacade {
            return DummyAuthenticatorFacade(
                scope = scope,
                loadingDuration = loadingDurationMillis.milliseconds,
                resetAfter = resetAfterMillis.milliseconds,
            )
        }
    }

    abstract val accounts: Flow<List<Account>>

    abstract val appStatus: Flow<AppStatus>

    /**
     * Add successfully connected accounts.
     *
     * Will lead to [appStatus] to switch to the [AppStatus.LoggingIn] case.
     */
    abstract suspend fun addAccounts(connectedAccounts: List<Account>)
}
