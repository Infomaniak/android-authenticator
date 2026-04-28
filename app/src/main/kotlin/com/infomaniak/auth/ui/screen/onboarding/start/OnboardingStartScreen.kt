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

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.MatomoAuthenticator.trackAccountEvent
import com.infomaniak.auth.lib.matomo.MatomoName
import com.infomaniak.auth.lib.models.UrlConstants.createAccountCancelUrl
import com.infomaniak.auth.lib.models.UrlConstants.createAccountSuccessUrl
import com.infomaniak.auth.lib.models.UrlConstants.createAccountUrl
import com.infomaniak.auth.ui.theme.AppDimens
import com.infomaniak.auth.ui.theme.AppShapes
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.auth.models.UserLoginResult
import com.infomaniak.core.auth.utils.LoginFlowController
import com.infomaniak.core.auth.utils.LoginUtils
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade.AccountsCheckingState
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade.AccountsCheckingStatus
import com.infomaniak.core.crossapplogin.back.ExternalAccount
import com.infomaniak.core.crossapplogin.front.components.CrossLoginBottomContent
import com.infomaniak.core.crossapplogin.front.components.NoCrossAppLoginAccountsContent
import com.infomaniak.core.crossapplogin.front.data.CrossLoginDefaults
import com.infomaniak.core.crossapplogin.front.previews.AccountsPreviewParameter
import com.infomaniak.core.network.ApiEnvironment
import com.infomaniak.core.onboarding.OnboardingScaffold
import com.infomaniak.core.onboarding.components.OnboardingComponents
import com.infomaniak.core.ui.compose.basics.ButtonStyle
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.coroutines.launch

@Composable
fun OnboardingStartScreen(
    onboardingStartViewModel: OnboardingStartViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val crossAppLoginFacade = onboardingStartViewModel.crossAppLoginFacade

    val accountsCheckingState by crossAppLoginFacade.accountsCheckingState.collectAsStateWithLifecycle()
    val skippedIds by crossAppLoginFacade.skippedAccountIds.collectAsStateWithLifecycle()
    val isButtonLoading by onboardingStartViewModel.isButtonLoading.collectAsStateWithLifecycle()

    // TODO[ik-auth]: Remove SignUp
    val isSignUpButtonLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val hostActivity = LocalActivity.current as ComponentActivity

    val loginFlowController = LoginUtils.rememberLoginFlowController(
        infomaniakLogin = onboardingStartViewModel.infomaniakLogin,
        userExistenceChecker = onboardingStartViewModel.accountUtils,
    ) { userLoginResult ->
        when (userLoginResult) {
            is UserLoginResult.Success -> scope.launch {
                onboardingStartViewModel.loginUsersIntoTheApp(listOf(userLoginResult.user))
            }
            is UserLoginResult.Failure -> scope.launch {
                snackbarHostState.showSnackbar(userLoginResult.errorMessage)
            }
            null -> Unit
        }

        if (userLoginResult !is UserLoginResult.Success) onboardingStartViewModel.stopLoadingLoginButtons()
    }

    LaunchedEffect(crossAppLoginFacade) {
        crossAppLoginFacade.activateUpdates(hostActivity)
    }

    OnboardingStartScreen(
        snackbarHostState = snackbarHostState,
        accountsCheckingState = { accountsCheckingState },
        skippedIds = { skippedIds },
        isLoginButtonLoading = { isButtonLoading },
        isSignUpButtonLoading = { isSignUpButtonLoading },
        onLoginRequest = { accounts ->
            if (accounts.isEmpty()) {
                openLoginWebView(onboardingStartViewModel, loginFlowController)
            } else {
                scope.launch { onboardingStartViewModel.connectSelectedAccounts(accounts, snackbarHostState) }
            }
        },
        onSaveSkippedAccounts = { crossAppLoginFacade.skippedAccountIds.value = it },
        onCreateAccount = { openAccountCreation(onboardingStartViewModel, loginFlowController) },
        onCancel = onboardingStartViewModel.cancelOnboarding
    )
}

@Composable
private fun OnboardingStartScreen(
    snackbarHostState: SnackbarHostState,
    accountsCheckingState: () -> AccountsCheckingState,
    skippedIds: () -> Set<Long>,
    isLoginButtonLoading: () -> Boolean,
    isSignUpButtonLoading: () -> Boolean,
    onLoginRequest: (accounts: List<ExternalAccount>) -> Unit,
    onSaveSkippedAccounts: (Set<Long>) -> Unit,
    onCreateAccount: () -> Unit,
    onCancel: (() -> Unit)? = null,
) {
    val pagerState = rememberPagerState(pageCount = { Page.entries.size })

    BackHandler(onCancel != null) {
        onCancel?.invoke()
    }

    OnboardingScaffold(
        pagerState = pagerState,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        onboardingPages = Page.entries.mapIndexed { index, page ->
            page.toOnboardingPage(pagerState, index)
        },
        bottomContent = { paddingValues ->
            OnboardingComponents.CrossLoginBottomContent(
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues),
                pagerState = pagerState,
                accountsCheckingState = accountsCheckingState,
                skippedIds = skippedIds,
                isLoginButtonLoading = isLoginButtonLoading,
                customization = CrossLoginDefaults.customize(
                    buttonStyle = CrossLoginDefaults.buttonType(object : ButtonStyle {
                        override val height: Dp = AppDimens.LargeButtonHeight
                        override val shape: Shape = AppShapes.LargeButtonShape
                    })
                ),
                onContinueWithSelectedAccounts = onLoginRequest,
                onUseAnotherAccountClicked = { onLoginRequest(emptyList()) },
                onSaveSkippedAccounts = onSaveSkippedAccounts,
                noCrossAppLoginAccountsContent = NoCrossAppLoginAccountsContent.accountRequired(
                    onLogin = { onLoginRequest(emptyList()) },
                    onCreateAccount = onCreateAccount,
                    isLoginButtonLoading = isLoginButtonLoading,
                    isSignUpButtonLoading = isSignUpButtonLoading,
                )
            )
        }
    )
}

private fun openLoginWebView(
    onboardingStartViewModel: OnboardingStartViewModel,
    loginFlowController: LoginFlowController
) {
    trackAccountEvent(MatomoName.OpenLoginWebview)
    onboardingStartViewModel.startLoadingLoginButtons()
    loginFlowController.login()
}

private fun openAccountCreation(
    onboardingStartViewModel: OnboardingStartViewModel,
    loginFlowController: LoginFlowController
) {
    val host = ApiEnvironment.current.host
    trackAccountEvent(MatomoName.OpenCreationWebview)
    onboardingStartViewModel.startLoadingLoginButtons()
    loginFlowController.createAccount(
        createAccountUrl = createAccountUrl(host),
        successHost = createAccountSuccessUrl(host),
        cancelHost = createAccountCancelUrl(host)
    )
}

@PreviewSmallWindow
@Composable
private fun OnboardingStartScreenPreview(
    @PreviewParameter(AccountsPreviewParameter::class) accounts: List<ExternalAccount>
) {
    AuthenticatorTheme {
        OnboardingStartScreen(
            snackbarHostState = SnackbarHostState(),
            accountsCheckingState = {
                AccountsCheckingState(AccountsCheckingStatus.Checking, checkedAccounts = accounts)
            },
            skippedIds = { emptySet() },
            isLoginButtonLoading = { false },
            isSignUpButtonLoading = { false },
            onLoginRequest = {},
            onSaveSkippedAccounts = {},
            onCreateAccount = {},
        )
    }
}
