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
package com.infomaniak.auth.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.basics.Dimens
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

@Composable
fun HomeScreen(onAccountClicked: (FakeAccount) -> Unit) {
    //TODO get accounts from DB
    val accounts = listOf(
        FakeAccount(
            name = "Laura Snow",
            email = "laura.snow@ik.me",
            securityLevel = AccountSecurityLevel.Secured,
        ),
        FakeAccount(
            name = "Laura Snow",
            email = "laura.snow@domain.com",
            securityLevel = AccountSecurityLevel.Warning,
        ),
        FakeAccount(
            name = "Laura Snow",
            email = "laura.snow@subdomain.com",
            securityLevel = AccountSecurityLevel.Danger,
        ),
    )

    val hasUnsecuredAccounts: Boolean by remember(accounts) {
        derivedStateOf { accounts.any { it.securityLevel != AccountSecurityLevel.Secured } }
    }

    SinglePaneScaffold(
        topBar = {
            InfomaniakAuthenticatorTopAppBar(isCentered = false, isBackgroundTransparent = true)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            if (hasUnsecuredAccounts) ActionRequired()

            val state = rememberPullToRefreshState()
            var isRefreshing by remember { mutableStateOf(false) }
            // TODO Handle the refresh correctly when we'll have real data to fetch
            LaunchedEffect(isRefreshing) {
                if (isRefreshing) {
                    delay(2_000)
                    isRefreshing = false
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                state = state,
                onRefresh = { isRefreshing = true },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Margin.Small)
                ) {
                    accounts.forEach { account ->
                        key(account.email) {
                            AccountItem(account, onClick = { account -> onAccountClicked(account) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionRequired() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium, vertical = Margin.Large),
        colors = CardDefaults.cardColors(containerColor = AuthenticatorTheme.customColors.actionRequiredBackground),
        border = BorderStroke(1.dp, AuthenticatorTheme.customColors.actionRequiredPrimary),
        shape = RoundedCornerShape(Dimens.largeCornerRadius),
    ) {
        Row(modifier = Modifier.padding(Margin.Small), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.alert),
                contentDescription = null,
                tint = AuthenticatorTheme.customColors.actionRequiredPrimary,
            )
            Text(
                modifier = Modifier.padding(start = Margin.Small),
                text = stringResource(R.string.actionRequiredDescription)
            )
        }
    }
}

@Composable
private fun AccountItem(account: FakeAccount, onClick: (FakeAccount) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .clip(RoundedCornerShape(Dimens.largeCornerRadius))
            .clickable(onClick = { onClick(account) }),
        colors = CardDefaults.cardColors(containerColor = AuthenticatorTheme.customColors.accountItemBackground),
    ) {
        Row(
            modifier = Modifier.padding(vertical = Margin.Small, horizontal = Margin.Medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            //TODO Use initial avatar here (or an image if it exist ?)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.avatarContentDescription),
                modifier = Modifier
                    .size(Dimens.bigAvatarSize)
                    .clip(CircleShape)
                    .background(AuthenticatorTheme.materialColors.surfaceContainerHighest)
            )
            Column(modifier = Modifier.padding(start = Margin.Medium)) {
                Text(text = account.name)
                Text(text = account.email)
            }
            Spacer(modifier = Modifier.weight(1f))
            //TODO Add a different content description when we're sure what represent each state
            Icon(
                modifier = Modifier.padding(end = Margin.Mini),
                painter = painterResource(id = account.securityLevel.iconResId),
                contentDescription = stringResource(R.string.accountSecurityLevelContentDescription),
                tint = account.securityLevel.iconTint(),
            )
            Icon(painterResource(R.drawable.right_arrow), null)
        }
    }
}

enum class AccountSecurityLevel(val iconResId: Int, val iconTint: @Composable () -> Color) {
    Secured(iconResId = R.drawable.shield_check, iconTint = { AuthenticatorTheme.customColors.accountSecured }),
    Warning(iconResId = R.drawable.shield_check, iconTint = { AuthenticatorTheme.customColors.accountWarning }),
    Danger(iconResId = R.drawable.shield_exclamation_mark, iconTint = { AuthenticatorTheme.customColors.accountWarning }),
}

@Serializable
data class FakeAccount(val name: String, val email: String, val securityLevel: AccountSecurityLevel)

@PreviewSmallWindow
@Composable
fun HomeScreenPreview() {
    AuthenticatorTheme {
        HomeScreen {}
    }
}
