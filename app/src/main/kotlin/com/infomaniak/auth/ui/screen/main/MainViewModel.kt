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
package com.infomaniak.auth.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomaniak.auth.data.preferences.PermissionPreferences
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.lib.repository.AppSettingsRepository
import com.infomaniak.auth.utils.AccountUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    appSettingsRepository: AppSettingsRepository,
    authenticatorFacade: AuthenticatorFacade,
    private val accountUtils: AccountUtils,
) : ViewModel() {
    val appStatus = authenticatorFacade.appStatus

    val accountsWithPasswordUpdate: Flow<List<Account>> = authenticatorFacade.accounts.map { accounts ->
        accounts.filter {
            when (val status = it.status) {
                is Account.Status.LoggedIn if (status.passwordChangedAck != null) -> true
                else -> false
            }
        }
    }

    val accountsDisconnected: Flow<List<Account>> = authenticatorFacade.accounts.map { accounts ->
        accounts.filter { it.status is Account.Status.NotConnected.Disconnected }
    }

    val isAppLocked = appSettingsRepository.getSettings().mapNotNull { it?.isAppLockEnabled }
    val hasTriggeredNotificationPermission: StateFlow<Boolean> = flow {
        emitAll(PermissionPreferences().hasTriggeredNotificationPermissionFlow)
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onNotificationPermissionTriggered() {
        viewModelScope.launch {
            PermissionPreferences().hasTriggeredNotificationPermission = true
        }
    }

    fun removeUser(accountId: Int) {
        viewModelScope.launch {
            accountUtils.removeUser(accountId)
        }
    }
}
