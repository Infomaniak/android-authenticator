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
package com.infomaniak.auth.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.infomaniak.multiplatform_authenticator.core.Account
import com.infomaniak.auth.ui.previewparameter.fakeAccounts
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.basics.Dimens
import com.infomaniak.core.ui.compose.basics.Typography
import com.infomaniak.core.ui.compose.margin.Margin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountRow(
    account: Account,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            modifier = Modifier
                .size(Dimens.bigAvatarSize)
                .clip(CircleShape),
            account = account,
        )

        Spacer(Modifier.width(Margin.Mini))

        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Margin.Mini),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = account.fullName,
                    style = Typography.bodyMedium,
                )
            }
            Text(
                text = account.email,
                style = Typography.bodyRegular,
            )
        }
    }
}

@Preview
@Composable
private fun AccountRowPreview() {
    AuthenticatorTheme {
        Surface {
            Row {
                AccountRow(account = fakeAccounts.first())
            }
        }
    }
}
