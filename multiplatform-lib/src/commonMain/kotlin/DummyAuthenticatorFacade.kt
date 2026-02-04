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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

internal class DummyAuthenticatorFacade : AuthenticatorFacade() {
    override val accounts: Flow<List<Account>>

    private var _accounts: List<Account> by MutableStateFlow<List<Account>>(emptyList()).also {
        accounts = it.asStateFlow()
    }::value

    override val appStatus: Flow<AppStatus> = flow {
        emit(AppStatus.SetupComplete)
        //TODO[ik-auth]: Add an in-memory demo version that goes through all the possible states
    }

    override suspend fun addAccounts(connectedAccounts: Map<Account, String>) {
        _accounts += connectedAccounts.keys
    }
}
