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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.ui.components.Avatar
import com.infomaniak.auth.ui.previewparameter.AccountPreviewParameter
import com.infomaniak.auth.ui.theme.AppDimens.DefaultCornerRadius
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.ui.compose.basics.Dimens
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun AccountItem(
    account: Account,
    user: User?,
    onClick: (Account) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
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
            StatusIcon(account.status)
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun StatusIcon(status: Account.Status) {
    when (status) {
        is Account.Status.NotConnected -> {
            Icon(
                modifier = Modifier.padding(end = Margin.Mini),
                painter = painterResource(id = R.drawable.alert),
                contentDescription = stringResource(R.string.warningIconContentDescription),
                tint = AuthenticatorTheme.customColors.iconTintDisconnected,
            )
        }
        is Account.Status.LoggedIn if !status.isSecured -> {
            Icon(
                modifier = Modifier.padding(end = Margin.Mini),
                painter = painterResource(id = R.drawable.shield_exclamation_mark),
                contentDescription = stringResource(R.string.accountSecurityLevelContentDescription),
                tint = AuthenticatorTheme.customColors.iconTintWarning,
            )
        }
        else -> {
            Icon(
                modifier = Modifier.padding(end = Margin.Mini),
                painter = painterResource(id = R.drawable.shield_check),
                contentDescription = stringResource(R.string.securedIconContentDescription),
                tint = AuthenticatorTheme.customColors.iconTintSuccess,
            )
        }
    }
}

@PreviewSmallWindow
@Composable
private fun AccountItemPreview(
    @PreviewParameter(AccountPreviewParameter::class) accountPairs: Pair<Account, User>
) {
    val (account, user) = accountPairs
    AuthenticatorTheme {
        Surface {
            AccountItem(account, user, onClick = { })
        }
    }
}
