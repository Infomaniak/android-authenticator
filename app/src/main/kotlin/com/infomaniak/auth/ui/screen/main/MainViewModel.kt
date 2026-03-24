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
import com.infomaniak.auth.lib.AppStatus
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.lib.repository.AppSettingsRepository
import com.infomaniak.auth.manager.AccountUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    appSettingsRepository: AppSettingsRepository,
    private val authenticatorFacade: AuthenticatorFacade,
    private val accountUtils: AccountUtils,
) : ViewModel() {

    val uiState = flow {
        emit(UiState.Ready(accountUtils.isUserConnected()))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Loading)

    val appStatus = authenticatorFacade.appStatus
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppStatus.LoginRequired.NotMigrating)

    val isAppLocked = appSettingsRepository.getSettings().mapNotNull { it?.isAppLockEnabled }

    sealed interface UiState {
        data object Loading : UiState
        data class Ready(val isUserConnected: Boolean) : UiState
    }
}
