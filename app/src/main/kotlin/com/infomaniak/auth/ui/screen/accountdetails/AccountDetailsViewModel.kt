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
package com.infomaniak.auth.ui.screen.accountdetails

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.manager.AccountUtils
import com.infomaniak.core.auth.models.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val accountUtils: AccountUtils,
    private val authenticatorFacade: AuthenticatorFacade,
) : ViewModel() {
    private val accountIdFlow = MutableSharedFlow<Long>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val uiState: StateFlow<AccountDetailsUiState> = accountIdFlow
        .flatMapLatest { id ->
            combine(
                accountUtils.users,
                authenticatorFacade.accounts
            ) { users, accounts ->
                val user = users.find { it.id.toLong() == id }
                val account = accounts.find { it.id == id }

                if (account != null && user != null) {
                    AccountDetailsUiState.Success(account, user)
                } else {
                    AccountDetailsUiState.Error
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AccountDetailsUiState.Loading
        )

    val accountRemovedChannel = Channel<Unit>(Channel.CONFLATED)

    fun fetchAccountDetails(accountId: Long) {
        accountIdFlow.tryEmit(accountId)
    }

    fun removeAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            accountIdFlow.first()
                .let { accountId -> accountUtils.users.first().firstOrNull { it.id.toLong() == accountId } }
                ?.let { user ->
                    authenticatorFacade.removeAccount(user.apiToken.accessToken, user.id.toLong())
                    accountUtils.removeUser(user.id)
                    accountRemovedChannel.send(Unit)
                }
        }
    }
}

@Immutable
sealed interface AccountDetailsUiState {
    data object Loading : AccountDetailsUiState
    data class Success(val account: Account, val user: User) : AccountDetailsUiState
    data object Error : AccountDetailsUiState
}
