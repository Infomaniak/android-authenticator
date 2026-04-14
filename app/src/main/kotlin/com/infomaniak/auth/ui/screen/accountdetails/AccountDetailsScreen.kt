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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.NotConnectedAction
import com.infomaniak.auth.lib.models.UrlConstants
import com.infomaniak.auth.lib.models.UrlConstants.ACTIVITY_MANAGER_URL
import com.infomaniak.auth.lib.models.UrlConstants.SETTINGS_MANAGER_URL
import com.infomaniak.auth.ui.components.Avatar
import com.infomaniak.auth.ui.components.ButtonStyle
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.components.OptionItemType
import com.infomaniak.auth.ui.components.OptionsSection
import com.infomaniak.auth.ui.components.StatusCard
import com.infomaniak.auth.ui.components.StatusCardVariant
import com.infomaniak.auth.ui.previewparameter.AccountPreviewParameter
import com.infomaniak.auth.ui.screen.accountdetails.AccountStatus.Companion.toAccountStatus
import com.infomaniak.auth.ui.theme.AppDimens.DefaultCornerRadius
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.network.ApiEnvironment
import com.infomaniak.core.ui.compose.basics.Typography
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import com.infomaniak.core.webview.ui.WebViewActivity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay

@Composable
fun AccountDetailsScreen(
    accountId: Long,
    onBackPressed: () -> Unit,
    onLoginPressed: (String) -> Unit,
    viewModel: AccountDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchAccountDetails(accountId)
    }

    LaunchedEffect(Unit) {
        viewModel.accountRemovedChannel.receive()
        onBackPressed()
    }

    AccountDetailsScreen(
        uiState = { uiState },
        onLoginPressed = onLoginPressed,
        onBackPressed = onBackPressed,
        onChallengesRefreshClicked = {
            viewModel.refreshChallenges(accountId)
        },
        onRemoveAccountClicked = {
            viewModel.removeAccount()
        }
    )
}

@Composable
fun AccountDetailsScreen(
    uiState: () -> AccountDetailsUiState,
    onLoginPressed: (String) -> Unit,
    onBackPressed: () -> Unit,
    onChallengesRefreshClicked: () -> Unit,
    onRemoveAccountClicked: () -> Unit,
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
                    onRemoveAccountClicked = onRemoveAccountClicked
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
    onLoginPressed: (String) -> Unit,
    onChallengesRefreshClicked: () -> Unit,
    onRemoveAccountClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        Header(account, user)
        SecurityCheck(account.status.toAccountStatus())
        if (account.status != Account.Status.LoggedIn) {
            var hasLogin by remember { mutableStateOf(false) }
            ActionRequired(
                hasLogin,
                logIn = {
                    hasLogin = true
                    val status = account.status as Account.Status.NotConnected
                    val legacyAccount = (status.action as NotConnectedAction.ReLogin).legacyAccount
                    onLoginPressed(legacyAccount.email)
                }
            )
        }
        SettingsSections(
            accountStatus = account.status,
            user = user,
            onChallengesRefreshClicked = onChallengesRefreshClicked,
            onRemoveAccountClicked = onRemoveAccountClicked
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
            Text(text = account.fullName, style = Typography.h1)
            Text(text = account.email)
        }
    }
}

@Composable
private fun SecurityCheck(accountStatus: AccountStatus) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Margin.Large)
            .padding(horizontal = Margin.Medium),
        shape = RoundedCornerShape(DefaultCornerRadius),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = AuthenticatorTheme.customColors.sectionBackground)
    ) {
        Column(
            modifier = Modifier.padding(Margin.Medium),
            verticalArrangement = Arrangement.spacedBy(Margin.Mini)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(accountStatus.titleResId),
                    style = if (accountStatus.descriptionResId != null) Typography.h2 else LocalTextStyle.current
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(accountStatus.iconResId),
                    contentDescription = null,
                    tint = accountStatus.iconTint?.invoke() ?: AuthenticatorTheme.customColors.iconTintWarning,
                )
            }
            accountStatus.descriptionResId?.let {
                Text(text = stringResource(accountStatus.descriptionResId))
            }
        }
    }
}

private data class ActionRequiredConfiguration(
    val text: String,
    val iconColor: Color,
    val iconRes: Int,
    val statusCardVariant: StatusCardVariant
)

@Composable
private fun ActionRequired(hasLogin: Boolean, logIn: () -> Unit) {
    val configuration = if (hasLogin) {
        ActionRequiredConfiguration(
            text = stringResource(R.string.errorLoginFailed, 0),
            iconRes = R.drawable.triangle_alert,
            iconColor = AuthenticatorTheme.customColors.iconTintError,
            statusCardVariant = StatusCardVariant.Error
        )
    } else {
        ActionRequiredConfiguration(
            text = stringResource(R.string.accountNotConnectedWarningTitle),
            iconRes = R.drawable.alert,
            iconColor = AuthenticatorTheme.customColors.iconTintWarning,
            statusCardVariant = StatusCardVariant.Warning
        )
    }

    StatusCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .padding(top = Margin.Large),
        shape = RoundedCornerShape(DefaultCornerRadius),
        variant = configuration.statusCardVariant,
    ) {
        Row(modifier = Modifier.padding(Margin.Medium), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(configuration.iconRes),
                contentDescription = null,
                tint = configuration.iconColor
            )
            Text(
                modifier = Modifier.padding(start = Margin.Small),
                text = configuration.text,
            )
        }

        if (hasLogin) ContactSupportButton()
        LogInAgainButton(hasLogin, logIn = logIn)
    }
}

@Composable
private fun ContactSupportButton() {
    LargeButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .padding(bottom = Margin.Medium),
        title = stringResource(R.string.contactSupportTitle),
        style = ButtonStyle.Primary,
        onClick = {}
    )
}

@Composable
private fun LogInAgainButton(hasLoggedInWithError: Boolean, logIn: () -> Unit) {
    LargeButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .padding(bottom = Margin.Medium),
        title = stringResource(R.string.logInButton),
        style = if (hasLoggedInWithError) ButtonStyle.Tertiary else ButtonStyle.Primary,
        onClick = {
            // TODO An error appear if we already tried to log in on figma so for now, we're doing like this
            logIn()
        }
    )
}

@Composable
private fun SettingsSections(
    accountStatus: Account.Status,
    user: User?,
    onChallengesRefreshClicked: () -> Unit,
    onRemoveAccountClicked: () -> Unit,
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
        onChallengesRefreshClicked()
        isRefreshing = true
    }

    val firstSectionItem = if (accountStatus == Account.Status.LoggedIn) {
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
                        WebViewActivity.startActivity(
                            context = context,
                            url = UrlConstants.autologUrl(host, UrlConstants.managerUrl(host = host, ACTIVITY_MANAGER_URL)),
                            headers = mapOf("Authorization" to "Bearer ${user.apiToken.accessToken}"),
                        )
                    },
                ),
            )
            add(
                OptionItemType.WithRightIcon(
                    stringResId = R.string.accountSettingsButton,
                    rightIconResId = R.drawable.square_arrow_up,
                    onClick = {
                        WebViewActivity.startActivity(
                            context = context,
                            url = UrlConstants.autologUrl(host = host, UrlConstants.managerUrl(host, SETTINGS_MANAGER_URL)),
                            headers = mapOf("Authorization" to "Bearer ${user.apiToken.accessToken}"),
                        )
                    },
                )
            )
        }
        add(
            OptionItemType.Default(
                stringResId = R.string.disconnectButton,
                textColor = AuthenticatorTheme.materialColors.error,
                onClick = {
                    onRemoveAccountClicked()
                }
            ),
        )

    }.toPersistentList()

    OptionsSection(
        modifier = modifier,
        sections = persistentListOf(firstSectionItem, secondSectionItems)
    )
}

private enum class AccountStatus(
    val titleResId: Int,
    val descriptionResId: Int? = null,
    val iconResId: Int,
    val iconTint: @Composable (() -> Color)? = null
) {
    Secured(
        titleResId = R.string.accountProtected,
        iconResId = R.drawable.shield_check,
        iconTint = { AuthenticatorTheme.customColors.iconTintSuccess }
    ),
    PartiallyProtected(
        titleResId = R.string.accountPartiallyProtectedTitle,
        descriptionResId = R.string.accountPartiallyProtectedDescription,
        iconResId = R.drawable.shield_exclamation_mark,
        iconTint = { AuthenticatorTheme.customColors.iconTintWarning }
    ),
    Disconnected(
        titleResId = R.string.disconnectSuccess,
        iconResId = R.drawable.circle_cross,
        iconTint = { AuthenticatorTheme.customColors.iconTintDisconnected }
    );

    companion object {
        fun Account.Status.toAccountStatus() = when (this) {
            Account.Status.LoggedIn -> Secured
            is Account.Status.NotConnected if this.action is NotConnectedAction.ReLogin -> PartiallyProtected
            else -> Disconnected
        }
    }
}

@PreviewSmallWindow
@Composable
private fun AccountDetailsScreenPreview(
    @PreviewParameter(AccountPreviewParameter::class) accountPairs: Pair<Account, User>
) {
    AuthenticatorTheme {
        AccountDetailsScreen(
            uiState = { AccountDetailsUiState.Success(accountPairs.first, accountPairs.second) },
            onLoginPressed = {},
            onBackPressed = {},
            onChallengesRefreshClicked = {},
            onRemoveAccountClicked = {},
        )
    }
}
