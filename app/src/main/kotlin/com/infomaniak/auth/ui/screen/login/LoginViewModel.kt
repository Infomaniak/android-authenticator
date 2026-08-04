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
package com.infomaniak.auth.ui.screen.login

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.infomaniak.multiplatform_authenticator.core.Account
import com.infomaniak.multiplatform_authenticator.core.AppStatus
import com.infomaniak.multiplatform_authenticator.core.AuthenticatorFacade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticatorFacade: AuthenticatorFacade,
) : ViewModel() {
    fun uiStateForAccount(legacyAccountId: Long): Flow<LoginUiState> {
        return authenticatorFacade.accounts.mapNotNull { accounts ->
            accounts.firstOrNull { it.id == legacyAccountId }?.let { LoginUiState.Ready(it) }
        }
    }

    fun skipMigration() {
        val appStatus = authenticatorFacade.appStatusOrNull<AppStatus.LoginRequired.MustReLogin>() ?: return
        appStatus.skip()
    }
}

@Immutable
sealed interface LoginUiState {
    data object Loading : LoginUiState
    data class Ready(val legacyAccount: Account) : LoginUiState
}
