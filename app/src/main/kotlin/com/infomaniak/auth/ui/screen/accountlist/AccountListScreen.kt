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
package com.infomaniak.auth.ui.screen.accountlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.matomo.MatomoScreen
import com.infomaniak.auth.ui.components.StatusCard
import com.infomaniak.auth.ui.components.StatusCardVariant
import com.infomaniak.auth.ui.previewparameter.fakeAccountPairs
import com.infomaniak.auth.ui.screen.accountlist.AccountListViewModel.AccountListUiState
import com.infomaniak.auth.ui.theme.AppDimens.DefaultCornerRadius
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.auth.utils.MatomoTrackScreen
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.coroutines.delay

@Composable
fun AccountListScreen(
    onAccountClicked: (Account) -> Unit,
    viewModel: AccountListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MatomoTrackScreen(MatomoScreen.AccountListScreen)

    when (val state = uiState) {
        is AccountListUiState.Success -> {
            AccountListScreen(
                uiState = { state },
                onAccountClicked = onAccountClicked,
                onChallengesRefreshRequested = viewModel::refreshChallenges,
                onUserProfilesRefreshRequested = viewModel::refreshUserProfiles,
            )
        }
        is AccountListUiState.Loading -> Unit
    }
}

@Composable
fun AccountListScreen(
    uiState: () -> AccountListUiState.Success,
    onAccountClicked: (Account) -> Unit,
    onChallengesRefreshRequested: () -> Unit,
    onUserProfilesRefreshRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = uiState()
    val hasUnsecuredAccounts: Boolean by remember(state.accountPairs) {
        derivedStateOf {
            state.accountPairs.any { (first, _) ->
                (first.status as? Account.Status.LoggedIn)?.isSecured == false
            }
        }
    }
    val hasAccountMigrationIssue: Boolean by remember(state.accountPairs) {
        derivedStateOf { state.accountPairs.any { (first, _) -> first.status is Account.Status.NotConnected.ReLogin } }
    }

    Column(modifier = modifier) {
        val pullToRefreshState = rememberPullToRefreshState()
        var isRefreshing by remember { mutableStateOf(false) }
        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                delay(2_000)
                isRefreshing = false
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            state = pullToRefreshState,
            onRefresh = {
                isRefreshing = true
                onChallengesRefreshRequested()
                onUserProfilesRefreshRequested()
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = Margin.Medium),
                verticalArrangement = Arrangement.spacedBy(Margin.Small)
            ) {
                if (hasUnsecuredAccounts) ActionRequired()
                if (hasAccountMigrationIssue) MigrationWarning(
                    accountWithMigrationIssueCount = state.accountPairs.count { (account, _) ->
                        account.status is Account.Status.NotConnected.ReLogin
                    }
                )
                state.accountPairs.forEach { (account, user) ->
                    key(account.email) {
                        AccountItem(
                            modifier = Modifier.padding(horizontal = Margin.Medium),
                            account = account,
                            user = user,
                            onClick = { account -> onAccountClicked(account) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionRequired() {
    StatusCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium),
        shape = RoundedCornerShape(DefaultCornerRadius),
        variant = StatusCardVariant.Warning,
    ) {
        Row(modifier = Modifier.padding(Margin.Small), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.shield_exclamation_mark),
                contentDescription = null,
                tint = AuthenticatorTheme.customColors.iconTintWarning
            )
            Text(
                modifier = Modifier.padding(start = Margin.Small),
                text = stringResource(R.string.actionRequiredDescription)
            )
        }
    }
}

@Composable
private fun MigrationWarning(accountWithMigrationIssueCount: Int) {
    StatusCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium),
        shape = RoundedCornerShape(DefaultCornerRadius),
        variant = StatusCardVariant.Warning,
    ) {
        Row(modifier = Modifier.padding(Margin.Small), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.alert),
                contentDescription = null,
                tint = AuthenticatorTheme.customColors.iconTintWarning
            )
            Text(
                modifier = Modifier.padding(start = Margin.Small),
                text = pluralStringResource(R.plurals.migrationWarningDescription, accountWithMigrationIssueCount)
            )
        }
    }
}

@PreviewSmallWindow
@Composable
private fun AccountListScreenPreview() {
    AuthenticatorTheme {
        Scaffold { paddingValues ->
            AccountListScreen(
                modifier = Modifier.padding(paddingValues),
                uiState = { AccountListUiState.Success(fakeAccountPairs) },
                onAccountClicked = {},
                onChallengesRefreshRequested = {},
                onUserProfilesRefreshRequested = {},
            )
        }
    }
}
