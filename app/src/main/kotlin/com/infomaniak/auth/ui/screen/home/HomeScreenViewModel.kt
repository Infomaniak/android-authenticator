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
package com.infomaniak.auth.ui.screen.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.utils.AccountUtils
import com.infomaniak.core.auth.models.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    accountUtils: AccountUtils,
    authenticatorFacade: AuthenticatorFacade
) : ViewModel() {
    val uiState: StateFlow<HomeScreenUiState> = authenticatorFacade.accounts
        .combine(accountUtils.users) { accounts, users ->
            val usersMap = users.associateBy { it.id.toLong() }
            accounts.map { account ->
                account to usersMap[account.id]
            }
        }
        .map { accountPairs ->
            if (accountPairs.isEmpty()) HomeScreenUiState.Loading else HomeScreenUiState.Success(accountPairs.toPersistentList())
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            HomeScreenUiState.Loading
        )
}

@Immutable
sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data class Success(val accountPairs: ImmutableList<Pair<Account, User?>>) : HomeScreenUiState
}
