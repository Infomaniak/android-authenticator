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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.ui.components.AccountRow
import com.infomaniak.auth.ui.previewparameter.fakeAccounts
import com.infomaniak.core.ui.compose.basics.Dimens
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.common.R as RCore

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MigrationListAccounts(accounts: () -> List<Account>, ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium),
        verticalArrangement = Arrangement.spacedBy(Margin.Medium),
    ) {
        Text(
            text = pluralStringResource(RCore.plurals.myAccount, accounts().size),
            style = MaterialTheme.typography.titleLarge
        )
        LazyColumn {
            items(accounts(), key = { it.id }) { account ->
                BottomSheetItem(account = account)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomSheetItem(account: Account) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.buttonHeight),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Margin.Mini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AccountRow(account, Modifier.weight(1.0f))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        MigrationListAccounts(accounts = { fakeAccounts })
    }
}
