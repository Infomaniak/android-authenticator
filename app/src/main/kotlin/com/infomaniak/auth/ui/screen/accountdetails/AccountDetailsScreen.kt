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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.MatomoAuthenticator
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.matomo.MatomoCategory
import com.infomaniak.auth.lib.matomo.MatomoName
import com.infomaniak.auth.lib.matomo.MatomoScreen
import com.infomaniak.auth.lib.models.UrlConstants
import com.infomaniak.auth.lib.models.UrlConstants.ACTIVITY_MANAGER_URL
import com.infomaniak.auth.lib.models.UrlConstants.SETTINGS_MANAGER_URL
import com.infomaniak.auth.ui.components.Avatar
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.OptionItemType
import com.infomaniak.auth.ui.components.OptionsSection
import com.infomaniak.auth.ui.previewparameter.AccountPreviewParameter
import com.infomaniak.auth.ui.screen.accountdetails.AccountSecurityConfiguration.Companion.toSecurityConfiguration
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.auth.utils.MatomoTrackScreen
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.network.ApiEnvironment
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import java.net.URLEncoder

@Composable
fun AccountDetailsScreen(
    accountId: Long,
    onBackPressed: () -> Unit,
    onLoginPressed: (Long) -> Unit,
    onRemoveAccountClicked: (Long, DisconnectConfiguration) -> Unit,
    onOpenWebview: (String, ImmutableMap<String, String>, Boolean) -> Unit,
    viewModel: AccountDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MatomoTrackScreen(MatomoScreen.AccountDetailsScreen)

    LaunchedEffect(Unit) {
        viewModel.fetchAccountDetails(accountId)
    }

    AccountDetailsScreen(
        uiState = { uiState },
        onLoginPressed = onLoginPressed,
        onBackPressed = onBackPressed,
        onChallengesRefreshClicked = {
            viewModel.refreshChallenges(accountId)
        },
        onRemoveAccountClicked = onRemoveAccountClicked,
        onOpenWebview = onOpenWebview
    )
}

@Composable
fun AccountDetailsScreen(
    uiState: () -> AccountDetailsUiState,
    onLoginPressed: (Long) -> Unit,
    onBackPressed: () -> Unit,
    onChallengesRefreshClicked: () -> Unit,
    onRemoveAccountClicked: (Long, DisconnectConfiguration) -> Unit,
    onOpenWebview: (String, ImmutableMap<String, String>, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SinglePaneScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            InfomaniakAuthenticatorTopAppBar(
                withTitle = false,
                isCentered = false,
                onBackPressed = onBackPressed
            )
        }
    ) { paddingValues ->
        when (val uiState = uiState()) {
            is AccountDetailsUiState.Success -> {
                AccountDetailsContent(
                    paddingValues = paddingValues,
                    account = uiState.account,
                    user = uiState.user,
                    onLoginPressed = onLoginPressed,
                    onChallengesRefreshClicked = onChallengesRefreshClicked,
                    onRemoveAccountClicked = {
                        onRemoveAccountClicked(uiState.account.id, uiState.disconnectConfiguration)
                    },
                    onOpenWebview = onOpenWebview,
                )
            }
            is AccountDetailsUiState.Loading -> Unit
            is AccountDetailsUiState.Error -> Unit
        }
    }
}

@Composable
private fun AccountDetailsContent(
    paddingValues: PaddingValues,
    account: Account,
    user: User?,
    onLoginPressed: (Long) -> Unit,
    onChallengesRefreshClicked: () -> Unit,
    onRemoveAccountClicked: () -> Unit,
    onOpenWebview: (String, ImmutableMap<String, String>, Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        Header(account, user)
        AccountSecurityCard(
            configuration = account.status.toSecurityConfiguration(),
            token = user?.apiToken?.accessToken,
            onOpenWebview = onOpenWebview
        )
        ActionRequiredCard(account.status, onLoginPressed)
        SettingsSections(
            accountStatus = account.status,
            user = user,
            onChallengesRefreshClicked = onChallengesRefreshClicked,
            onRemoveAccountClicked = onRemoveAccountClicked,
            onOpenWebview = onOpenWebview,
        )
    }
}

@Composable
private fun Header(account: Account, user: User?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            account = account,
            user = user,
            modifier = Modifier
                .padding(horizontal = Margin.Medium)
                .size(40.dp)
                .clip(CircleShape)
                .background(AuthenticatorTheme.materialColors.surfaceContainerHighest)
        )
        Column {
            Text(text = account.fullName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium))
            Text(text = account.email)
        }
    }
}

@Composable
private fun SettingsSections(
    accountStatus: Account.Status,
    user: User?,
    onChallengesRefreshClicked: () -> Unit,
    onRemoveAccountClicked: () -> Unit,
    onOpenWebview: (String, ImmutableMap<String, String>, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val host = ApiEnvironment.current.host

    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(2_000)
            isRefreshing = false
        }
    }

    val onClickRefreshChallenges = {
        MatomoAuthenticator.trackEvent(MatomoCategory.Account, MatomoName.AskRefreshChallenge)
        onChallengesRefreshClicked()
        isRefreshing = true
    }

    val firstSectionItem = if (accountStatus is Account.Status.LoggedIn) {
        persistentListOf(
            OptionItemType.WithLoader(
                stringResId = R.string.refreshPendingLoginsButton,
                isLoading = isRefreshing,
                onClick = if (!isRefreshing) onClickRefreshChallenges else null,
            ),
        )
    } else persistentListOf()

    val secondSectionItems = buildList {
        if (user?.apiToken?.accessToken != null) {
            add(
                OptionItemType.WithRightIcon(
                    stringResId = R.string.activityHistoryButton,
                    rightIconResId = R.drawable.square_arrow_up,
                    onClick = {
                        MatomoAuthenticator.trackEvent(MatomoCategory.Account, MatomoName.OpenHistoryWebview)
                        val url = UrlConstants.autologUrl(
                            host,
                            URLEncoder.encode(UrlConstants.managerUrl(host = host, ACTIVITY_MANAGER_URL), "UTF-8")
                        )
                        val headers = persistentMapOf("Authorization" to "Bearer ${user.apiToken.accessToken}")
                        onOpenWebview(url, headers, false)
                    },
                ),
            )
            add(
                OptionItemType.WithRightIcon(
                    stringResId = R.string.accountSettingsButton,
                    rightIconResId = R.drawable.square_arrow_up,
                    onClick = {
                        MatomoAuthenticator.trackEvent(MatomoCategory.Account, MatomoName.OpenSettingsWebview)
                        val url = UrlConstants.autologUrl(
                            host = host,
                            URLEncoder.encode(UrlConstants.managerUrl(host, SETTINGS_MANAGER_URL), "UTF-8")
                        )
                        val headers = persistentMapOf("Authorization" to "Bearer ${user.apiToken.accessToken}")
                        onOpenWebview(url, headers, true)
                    },
                )
            )
        }
        add(
            OptionItemType.Default(
                stringResId = if (accountStatus is Account.Status.NotConnected) R.string.removeAccountButton else R.string.disconnectButton,
                textColor = AuthenticatorTheme.materialColors.error,
                onClick = {
                    MatomoAuthenticator.trackEvent(MatomoCategory.Account, MatomoName.Disconnect)
                    onRemoveAccountClicked()
                }
            ),
        )

    }.toPersistentList()

    Column(modifier.padding(top = Margin.Medium)) {
        OptionsSection(sections = persistentListOf(firstSectionItem, secondSectionItems))
    }
}

@PreviewSmallWindow
@Composable
private fun AccountDetailsScreenPreview(
    @PreviewParameter(AccountPreviewParameter::class) accountPairs: Pair<Account, User>
) {
    AuthenticatorTheme {
        AccountDetailsScreen(
            uiState = {
                AccountDetailsUiState.Success(
                    account = accountPairs.first,
                    user = accountPairs.second,
                    disconnectConfiguration = DisconnectConfiguration.DisconnectSecuredAccount
                )
            },
            onLoginPressed = {},
            onBackPressed = {},
            onChallengesRefreshClicked = {},
            onRemoveAccountClicked = { _, _ -> },
            onOpenWebview = { _, _, _ -> }
        )
    }
}
