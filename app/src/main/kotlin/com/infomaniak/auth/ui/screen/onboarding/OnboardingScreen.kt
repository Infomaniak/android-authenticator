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
package com.infomaniak.auth.ui.screen.onboarding

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.crossapplogin.back.BaseCrossAppLoginViewModel.AccountsCheckingState
import com.infomaniak.core.crossapplogin.back.BaseCrossAppLoginViewModel.AccountsCheckingStatus
import com.infomaniak.core.crossapplogin.back.ExternalAccount
import com.infomaniak.core.crossapplogin.front.components.CrossLoginBottomContent
import com.infomaniak.core.crossapplogin.front.components.NoCrossAppLoginAccountsContent
import com.infomaniak.core.crossapplogin.front.data.CrossLoginDefaults
import com.infomaniak.core.crossapplogin.front.previews.AccountsPreviewParameter
import com.infomaniak.core.onboarding.OnboardingScaffold
import com.infomaniak.core.onboarding.components.OnboardingComponents
import com.infomaniak.core.ui.compose.basics.CallableState
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun OnboardingScreen(
    crossAppLoginViewModel: CrossAppLoginViewModel = viewModel(),
    loginRequest: CallableState<List<ExternalAccount>>,
    navigateToHome: () -> Unit
) {
    val accountsCheckingState by crossAppLoginViewModel.accountsCheckingState.collectAsStateWithLifecycle()
    val skippedIds by crossAppLoginViewModel.skippedAccountIds.collectAsStateWithLifecycle()

    val hostActivity = LocalActivity.current as ComponentActivity
    LaunchedEffect(crossAppLoginViewModel) {
        crossAppLoginViewModel.activateUpdates(hostActivity)
    }

    OnboardingScreen(
        accountsCheckingState = { accountsCheckingState },
        skippedIds = { skippedIds },
        isLoginButtonLoading = { loginRequest.isAwaitingCall.not() },
        onLoginRequest = { accounts -> loginRequest(accounts) },
        onSaveSkippedAccounts = { crossAppLoginViewModel.skippedAccountIds.value = it },
        onStartClicked = navigateToHome,
    )
}

@Composable
private fun OnboardingScreen(
    accountsCheckingState: () -> AccountsCheckingState,
    skippedIds: () -> Set<Long>,
    isLoginButtonLoading: () -> Boolean,
    onLoginRequest: (accounts: List<ExternalAccount>) -> Unit,
    onSaveSkippedAccounts: (Set<Long>) -> Unit,
    onStartClicked: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { Page.entries.size })

    OnboardingScaffold(
        pagerState = pagerState,
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
                    colors = CrossLoginDefaults.colors(
                        titleColor = MaterialTheme.colorScheme.primary,
                        descriptionColor = MaterialTheme.colorScheme.secondary
                    ),
                ),
                onContinueWithSelectedAccounts = { selectedAccounts -> onLoginRequest(selectedAccounts) },
                onUseAnotherAccountClicked = { onLoginRequest(emptyList()) },
                onSaveSkippedAccounts = onSaveSkippedAccounts,
                noCrossAppLoginAccountsContent = NoCrossAppLoginAccountsContent.accountOptional { onStartClicked() }
            )
        }
    )
}

@PreviewSmallWindow
@Composable
private fun OnboardingScreenPreview(
    @PreviewParameter(AccountsPreviewParameter::class) accounts: List<ExternalAccount>
) {
    AuthenticatorTheme {
        OnboardingScreen(
            accountsCheckingState = {
                AccountsCheckingState(AccountsCheckingStatus.Checking, checkedAccounts = accounts)
            },
            skippedIds = { emptySet() },
            isLoginButtonLoading = { true },
            onLoginRequest = {},
            onSaveSkippedAccounts = {},
            onStartClicked = {},
        )
    }
}
