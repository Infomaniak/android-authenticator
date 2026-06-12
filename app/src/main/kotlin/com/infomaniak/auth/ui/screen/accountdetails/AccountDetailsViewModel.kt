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
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.lib.models.UrlConstants
import com.infomaniak.auth.utils.AccountUtils
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.twofactorauth.back.TwoFactorAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val accountUtils: AccountUtils,
    private val authenticatorFacade: AuthenticatorFacade,
    private val twoFactorAuthManager: TwoFactorAuthManager,
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

                if (account != null) {
                    AccountDetailsUiState.Success(
                        account = account,
                        user = user,
                        disconnectConfiguration = genDisconnectConfiguration(status = account.status)
                    )
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

    fun fetchAccountDetails(accountId: Long) {
        accountIdFlow.tryEmit(accountId)
    }

    private fun genDisconnectConfiguration(status: Account.Status): DisconnectConfiguration {
        return when (status) {
            is Account.Status.LoggedIn -> {
                if (status.yolo) {
                    DisconnectConfiguration.DisconnectSecuredAccount
                } else {
                    DisconnectConfiguration.DisconnectPartiallySecuredAccount
                }
            }
            else -> DisconnectConfiguration.DisconnectNotConnectedAccount
        }
    }

    fun refreshChallenges(userId: Long) {
        twoFactorAuthManager.refreshChallengeNow(userId)
    }
}

@Immutable
sealed interface AccountDetailsUiState {
    data object Loading : AccountDetailsUiState
    data class Success(
        val account: Account,
        val user: User?,
        val disconnectConfiguration: DisconnectConfiguration
    ) : AccountDetailsUiState

    data object Error : AccountDetailsUiState
}

@Serializable
enum class DisconnectConfiguration(
    val warningTitleResId: Int,
    val warningDescriptionResId: Int,
    val confirmationTitleResId: Int,
    val confirmationDescriptionResId: Int,
    val neutralButtonStringResId: Int,
    val criticalButtonStringResId: Int,
    val dismissHelpUrl: String
) {
    DisconnectSecuredAccount(
        warningTitleResId = R.string.disconnectAccountWarningTitle,
        warningDescriptionResId = R.string.disconnectAccountWarningDescription,
        confirmationTitleResId = R.string.disconnectAccountTitle,
        confirmationDescriptionResId = R.string.disconnectAccountOnThisDeviceDescription,
        neutralButtonStringResId = R.string.checkMyMethodsButton,
        criticalButtonStringResId = R.string.disconnectCriticalButton,
        dismissHelpUrl = UrlConstants.SETTINGS_ACCOUNT_SECURITY_URL,
    ),

    DisconnectPartiallySecuredAccount(
        warningTitleResId = R.string.disconnectAccountPartiallySecuredWarningTitle,
        warningDescriptionResId = R.string.disconnectAccountPartiallySecuredWarningDescription,
        confirmationTitleResId = R.string.disconnectAccountTitle,
        confirmationDescriptionResId = R.string.disconnectPartiallySecuredDescription,
        neutralButtonStringResId = R.string.disconnectAccountAdd2faButton,
        criticalButtonStringResId = R.string.disconnectCriticalButton,
        dismissHelpUrl = UrlConstants.SETTINGS_2FA_MANAGER_URL,
    ),

    DisconnectNotConnectedAccount(
        warningTitleResId = R.string.removeAccountWarningTitle,
        warningDescriptionResId = R.string.removeAccountWarningDescription,
        confirmationTitleResId = R.string.removeAccountTitle,
        confirmationDescriptionResId = R.string.removeAccountDescription,
        neutralButtonStringResId = R.string.checkMyMethodsButton,
        criticalButtonStringResId = R.string.removeAccountTitle,
        dismissHelpUrl = UrlConstants.SETTINGS_ACCOUNT_SECURITY_URL,
    )
}
