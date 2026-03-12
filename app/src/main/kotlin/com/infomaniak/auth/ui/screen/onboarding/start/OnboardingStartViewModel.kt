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
package com.infomaniak.auth.ui.screen.onboarding.start

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewModelScope
import com.infomaniak.auth.BuildConfig
import com.infomaniak.auth.MatomoAuthenticator.trackAccountEvent
import com.infomaniak.auth.lib.managers.AuthenticatorManager
import com.infomaniak.auth.lib.matomo.MatomoName
import com.infomaniak.auth.lib.repository.AccountsRepository
import com.infomaniak.auth.lib.room.accounts.Account
import com.infomaniak.auth.lib.room.accounts.StatusEntity
import com.infomaniak.auth.manager.AccountUtils
import com.infomaniak.core.auth.models.UserLoginResult
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.auth.utils.LoginUtils
import com.infomaniak.core.crossapplogin.back.BaseCrossAppLoginViewModel
import com.infomaniak.core.crossapplogin.back.ExternalAccount
import com.infomaniak.lib.login.InfomaniakLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingStartViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val infomaniakLogin: InfomaniakLogin,
    val accountUtils: AccountUtils,
    val authenticatorManager: AuthenticatorManager,
    val accountsRepository: AccountsRepository,
) : BaseCrossAppLoginViewModel(BuildConfig.APPLICATION_ID, BuildConfig.CLIENT_ID) {
    private val _isButtonLoading = MutableStateFlow(false)
    val isButtonLoading = _isButtonLoading.asStateFlow()

    private val _onLoginFinishedEvent = MutableSharedFlow<Unit>()
    val onLoginFinishedEvent = _onLoginFinishedEvent.asSharedFlow()

    fun loginUsersIntoTheApp(users: List<User>) {
        trackAccountEvent(MatomoName.LoggedIn)
        viewModelScope.launch {
            users.forEach { user ->
                authenticatorManager.registerPasskey(user.apiToken.accessToken, user.id)
                addUserToAuthenticatorDB(user, StatusEntity.LoggedIn)
                accountUtils.addUser(user)
            }
            _onLoginFinishedEvent.emit(Unit)
        }
    }

    private suspend fun addUserToAuthenticatorDB(user: User, statusEntity: StatusEntity) {
        accountsRepository.upsertAccount(
            Account(
                id = user.id.toLong(),
                fullName = "${user.firstname} ${user.lastname}",
                initials = user.getInitials(),
                email = user.email,
                avatarUrl = user.avatar,
                status = statusEntity, // TODO How to add users here when login has failed ?
            )
        )
    }

    suspend fun connectSelectedAccounts(
        accounts: List<ExternalAccount>,
        snackbarHostState: SnackbarHostState,
    ) {
        startLoadingLoginButtons()
        val loginResult = attemptLogin(selectedAccounts = accounts)
        loginUsers(loginResult, snackbarHostState)
        loginResult.errorMessageIds.forEach { messageResId ->
            snackbarHostState.showSnackbar(context.resources.getString(messageResId))
        }
    }

    private suspend fun loginUsers(loginResult: LoginResult, snackbarHostState: SnackbarHostState) {
        val results = LoginUtils.getLoginResultsAfterCrossApp(loginResult.tokens, context, accountUtils)
        val users = buildList {
            results.forEach { result ->
                when (result) {
                    is UserLoginResult.Success -> add(result.user)
                    is UserLoginResult.Failure -> snackbarHostState.showSnackbar(result.errorMessage)
                }
            }
        }

        if (users.isEmpty()) {
            stopLoadingLoginButtons()
        } else {
            loginUsersIntoTheApp(users)
        }
    }

    fun startLoadingLoginButtons() {
        _isButtonLoading.value = true
    }

    fun stopLoadingLoginButtons() {
        _isButtonLoading.value = false
    }
}
