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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.ui.components.Avatar
import com.infomaniak.auth.ui.components.StatusCard
import com.infomaniak.auth.ui.components.StatusCardVariant
import com.infomaniak.auth.ui.previewparameter.fakeAccountPairs
import com.infomaniak.auth.ui.screen.accountlist.AccountSecurityLevel.Companion.toAccountSecurityLevel
import com.infomaniak.auth.ui.theme.AppDimens.DefaultCornerRadius
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.ui.compose.basics.Dimens
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.coroutines.delay

@Composable
fun AccountListScreen(
    onAccountClicked: (Account) -> Unit,
    viewModel: AccountListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is AccountListUiState.Success -> {
            AccountListScreen(
                uiState = { state },
                onAccountClicked = onAccountClicked,
                onChallengesRefreshRequested = viewModel::refreshChallenges,
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
    modifier: Modifier = Modifier
) {
    val state = uiState()
    val hasUnsecuredAccounts: Boolean by remember(state.accountPairs) {
        derivedStateOf { state.accountPairs.any { it.first.status.toAccountSecurityLevel() != AccountSecurityLevel.Secured } }
    }

    Column(modifier = modifier) {
        if (hasUnsecuredAccounts) ActionRequired()

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
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Margin.Small)
            ) {
                state.accountPairs.forEach { (account, user) ->
                    key(account.email) {
                        AccountItem(account, user, onClick = { account -> onAccountClicked(account) })
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
            .padding(horizontal = Margin.Medium, vertical = Margin.Large),
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
                text = stringResource(R.string.actionRequiredDescription)
            )
        }
    }
}

@Composable
private fun AccountItem(
    account: Account,
    user: User?,
    onClick: (Account) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .clip(RoundedCornerShape(DefaultCornerRadius))
            .clickable(onClick = { onClick(account) }),
        colors = CardDefaults.cardColors(containerColor = AuthenticatorTheme.customColors.sectionBackground),
    ) {
        Row(
            modifier = Modifier.padding(vertical = Margin.Small, horizontal = Margin.Medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Margin.Micro)
        ) {
            Avatar(
                account = account,
                user = user,
                modifier = Modifier
                    .size(Dimens.bigAvatarSize)
                    .clip(CircleShape)
                    .background(AuthenticatorTheme.materialColors.surfaceContainerHighest)
            )
            Column(
                modifier = Modifier
                    .padding(start = Margin.Medium)
                    .weight(1f),
            ) {
                Text(
                    text = account.fullName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = account.email,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier)
            Icon(
                modifier = Modifier.padding(end = Margin.Mini),
                painter = painterResource(id = account.status.toAccountSecurityLevel().iconResId),
                contentDescription = stringResource(R.string.accountSecurityLevelContentDescription),
                tint = account.status.toAccountSecurityLevel().iconTint(),
            )
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = null
            )
        }
    }
}

private enum class AccountSecurityLevel(val iconResId: Int, val iconTint: @Composable () -> Color) {
    Secured(iconResId = R.drawable.shield_check, iconTint = { AuthenticatorTheme.customColors.iconTintSuccess }),
    Warning(iconResId = R.drawable.shield_check, iconTint = { AuthenticatorTheme.customColors.iconTintWarning }),
    Danger(iconResId = R.drawable.shield_exclamation_mark, iconTint = { AuthenticatorTheme.customColors.iconTintWarning });

    companion object {
        fun Account.Status.toAccountSecurityLevel() = when (this) {
            Account.Status.LoggedIn -> Secured
            is Account.Status.NotConnected -> Danger //TODO: Shouldn't this show the exclamation mark too?
            else -> Warning // TODO: Use secure level to determine the status more precisely
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
            )
        }
    }
}
