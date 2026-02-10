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
package com.infomaniak.auth.ui.screen.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomaniak.auth.repository.AppSettingsRepository
import com.infomaniak.auth.room.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isNotificationEnabled: Boolean = false,
    val isAppLocked: Boolean = false,
    val theme: Theme? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = appSettingsRepository.getSettings()
        .map { settings ->
            SettingsUiState(
                isNotificationEnabled = settings?.isNotificationEnabled ?: false,
                isAppLocked = settings?.isAppLockEnabled ?: false,
                theme = settings?.theme,
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState())

    fun setIsNotificationEnabled(isNotificationEnabled: Boolean) {
        viewModelScope.launch { appSettingsRepository.setIsNotificationEnabled(isNotificationEnabled) }
    }

    fun setIsAppLockEnabled(isAppLocked: Boolean) {
        viewModelScope.launch { appSettingsRepository.setIsAppLockEnabled(isAppLocked) }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch { appSettingsRepository.setTheme(theme) }
    }
}
