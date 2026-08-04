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
package com.infomaniak.auth.ui.screen.onboarding.migration.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.multiplatform_authenticator.core.Account
import com.infomaniak.auth.ui.components.AccountRow
import com.infomaniak.auth.ui.components.Avatar
import com.infomaniak.auth.ui.previewparameter.fakeAccounts
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.basics.Dimens
import com.infomaniak.core.ui.compose.basics.Typography
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.common.R as RCore

@Composable
fun MigrationSelectAccounts(
    accounts: () -> List<Account>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accounts = accounts()
    val count = accounts.count()

    SelectedAccountsButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        when {
            count == 1 -> AccountRow(accounts.single(), Modifier.weight(1.0f))
            count > 1 -> MultipleAccounts(accounts, Modifier.weight(1.0f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedAccountsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = Margin.Medium),
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.buttonHeight),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(Dimens.largeCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            contentColor = AuthenticatorTheme.materialColors.onSurface,
            disabledContainerColor = Color.Transparent,
        ),
        onClick = onClick,
        contentPadding = contentPadding,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Margin.Mini),
            horizontalArrangement = Arrangement.spacedBy(Margin.Mini),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            content()

            Icon(
                painter = painterResource(R.drawable.chevron_down),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MultipleAccounts(
    accounts: List<Account>,
    modifier: Modifier = Modifier,
) {

    val count = accounts.count()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margin.Mini)
    ) {
        if (count == 2) {
            TwoAccountsView(accounts)
        } else {
            ThreeAccountsView(accounts)
        }

        Text(
            text = pluralStringResource(RCore.plurals.myAccount, count, count),
            style = Typography.bodyMedium,
        )
    }
}

@Composable
internal fun TwoAccountsView(accounts: List<Account>) {
    Box(
        modifier = Modifier.size(
            width = Dimens.avatarsBoxWidth,
            height = Dimens.avatarsBoxHeight,
        ),
    ) {
        // Right
        Avatar(
            modifier = Modifier
                .size(Dimens.avatarsBoxHeight)
                .align(Alignment.CenterEnd),
            account = accounts[1],
        )

        // Left
        Avatar(
            modifier = Modifier
                .size(Dimens.avatarsBoxHeight)
                .align(Alignment.CenterStart),
            account = accounts[0],
        )
    }
}

@Composable
internal fun ThreeAccountsView(accounts: List<Account>) {
    Box(contentAlignment = Alignment.Center) {
        Row {
            // Left
            Avatar(
                modifier = Modifier.size(Dimens.iconSize),
                account = accounts[1],
            )

            Spacer(Modifier.width(width = Dimens.avatarsBoxWidth - (Dimens.iconSize + Dimens.iconSize)))

            // Right
            Avatar(
                modifier = Modifier.size(Dimens.iconSize),
                account = accounts[2],
            )
        }

        // Center
        Avatar(
            modifier = Modifier.size(Dimens.avatarsBoxHeight),
            account = accounts[0],
        )
    }
}

@Preview
@Composable
private fun MultipleAccountsPreview() {
    AuthenticatorTheme {
        Surface {
            Row {
                MultipleAccounts(accounts = fakeAccounts)
            }
        }
    }
}

@Preview
@Composable
private fun MigrationSelectAccountsPreview() {
    AuthenticatorTheme {
        Surface {
            Row {
                MigrationSelectAccounts(
                    accounts = { fakeAccounts },
                    onClick = {},
                )
            }
        }
    }
}
