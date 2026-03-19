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

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.NotConnectedAction
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
import com.infomaniak.core.ui.compose.basics.Typography
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AccountDetailsScreen(
    accountId: Long,
    onBackPressed: () -> Unit,
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
        onBackPressed = onBackPressed,
        onRemoveAccountClicked = {
            viewModel.removeAccount()
        }
    )
}

@Composable
fun AccountDetailsScreen(
    uiState: () -> AccountDetailsUiState,
    onBackPressed: () -> Unit,
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
        when (val state = uiState()) {
            is AccountDetailsUiState.Success -> {
                AccountDetailsContent(
                    paddingValues = paddingValues,
                    account = state.account,
                    onRemoveAccountClicked = onRemoveAccountClicked
                )
            }
            is AccountDetailsUiState.Loading -> Unit
        }
    }
}

@Composable
private fun AccountDetailsContent(
    paddingValues: PaddingValues,
    account: Account,
    onRemoveAccountClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        Header(account)
        SecurityCheck(account.status.toAccountStatus())
        if (account.status != Account.Status.LoggedIn) {
            var hasLogin by remember { mutableStateOf(false) }
            ActionRequired(
                hasLogin,
                logIn = {
                    hasLogin = true
                }
            )
        }
        SettingsSections(
            accountStatus = account.status,
            onRemoveAccountClicked = onRemoveAccountClicked
        )
    }
}

@Composable
private fun Header(account: Account) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Avatar de l'utilisateur",
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
    StatusCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Margin.Large)
            .padding(horizontal = Margin.Medium),
        variant = StatusCardVariant.Neutral,
        shape = RoundedCornerShape(DefaultCornerRadius),
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
    onRemoveAccountClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstSectionItem = if (accountStatus == Account.Status.LoggedIn) {
        persistentListOf(
            OptionItemType.WithRightIcon(
                stringResId = R.string.refreshPendingLoginsButton,
                rightIconResId = R.drawable.right_indicator,
                onClick = {},
            ),
        )
    } else persistentListOf()

    val secondSectionItems = persistentListOf(
        OptionItemType.WithRightIcon(
            stringResId = R.string.activityHistoryButton,
            rightIconResId = R.drawable.square_arrow_up,
            onClick = {},
        ),
        OptionItemType.WithRightIcon(
            stringResId = R.string.accountSettingsButton,
            rightIconResId = R.drawable.square_arrow_up,
            onClick = {},
        ),
        OptionItemType.Default(
            stringResId = R.string.disconnectButton,
            textColor = AuthenticatorTheme.materialColors.error,
            onClick = {
                onRemoveAccountClicked()
            }
        ),
    )

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
    @PreviewParameter(AccountPreviewParameter::class) account: Account
) {
    AuthenticatorTheme {
        AccountDetailsScreen(
            uiState = { AccountDetailsUiState.Success(account) },
            onBackPressed = {},
            onRemoveAccountClicked = {},
        )
    }
}
