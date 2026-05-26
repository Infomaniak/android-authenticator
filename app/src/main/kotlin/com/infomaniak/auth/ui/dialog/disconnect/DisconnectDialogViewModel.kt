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
package com.infomaniak.auth.ui.dialog.disconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.utils.AccountUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DisconnectDialogViewModel @Inject constructor(
    private val accountUtils: AccountUtils,
    private val authenticatorFacade: AuthenticatorFacade,
) : ViewModel() {
    private val accountIdFlow = MutableSharedFlow<Long>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val userAccessToken = accountIdFlow
        .flatMapLatest { id ->
            combine(
                accountUtils.users,
                authenticatorFacade.accounts
            ) { users, accounts ->
                val user = users.find { it.id.toLong() == id }
                val account = accounts.find { it.id == id }

                if (account != null) user?.apiToken?.accessToken else null
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun fetchAccountDetails(accountId: Long) {
        accountIdFlow.tryEmit(accountId)
    }

    fun removeAccount(onAccountRemoved: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            accountIdFlow.first().let { accountId ->
                authenticatorFacade.removeAccount(token = userAccessToken.value, id = accountId)
                accountUtils.removeUser(accountId.toInt())
                withContext(Dispatchers.Main) {
                    onAccountRemoved()
                }
            }
        }
    }
}
