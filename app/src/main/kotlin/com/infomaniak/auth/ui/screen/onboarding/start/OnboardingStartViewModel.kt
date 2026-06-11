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
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomaniak.auth.MatomoAuthenticator.trackAccountEvent
import com.infomaniak.auth.lib.AppStatus
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.lib.matomo.MatomoName
import com.infomaniak.auth.utils.AccountUtils
import com.infomaniak.auth.utils.toSharedUser
import com.infomaniak.core.auth.models.UserLoginResult
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.auth.utils.LoginUtils
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade.LoginResult
import com.infomaniak.core.crossapplogin.back.ExternalAccount
import com.infomaniak.core.login.InfomaniakLogin
import com.infomaniak.core.ui.compose.basics.collectAsStateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingStartViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val infomaniakLogin: InfomaniakLogin,
    val accountUtils: AccountUtils,
    val authenticatorFacade: AuthenticatorFacade,
    val crossAppLoginFacade: CrossAppLoginFacade,
) : ViewModel() {
    private val _isButtonLoading = MutableStateFlow(false)
    val isButtonLoading = _isButtonLoading.asStateFlow()

    val cancelOnboarding: (() -> Unit)? by authenticatorFacade.appStatus
        .map { (it as? AppStatus.AddingAnAccount)?.cancel }
        .collectAsStateIn(scope = viewModelScope, initialValue = null)

    fun loginUsersIntoTheApp(users: List<User>) {
        trackAccountEvent(MatomoName.LoggedIn)
        viewModelScope.launch {
            users.forEach { user ->
                user.apiToken.isTemporary = true
                addUserToAuthenticatorDB(user)
            }
        }
    }

    private suspend fun addUserToAuthenticatorDB(user: User) {
        authenticatorFacade.addAccounts(listOf(user.toSharedUser()))
    }

    suspend fun connectSelectedAccounts(
        accounts: List<ExternalAccount>,
        snackbarHostState: SnackbarHostState,
    ) {
        startLoadingLoginButtons()
        val loginResult = crossAppLoginFacade.attemptLogin(selectedAccounts = accounts)
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
