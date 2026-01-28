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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun HomeScreen() {
    // TODO get accounts from DB
    val accounts = listOf(
        FakeAccount(
            name = "Laura Snow",
            email = "laura.snow@ik.me",
            isSafe = true,
        ),
        FakeAccount(
            name = "Laura Snow",
            email = "laura.snow@domain.com",
            isSafe = true,
        ),
    )

    SinglePaneScaffold(
        topBar = {
            InfomaniakAuthenticatorTopAppBar(isCentered = false, isBackgroundTransparent = true)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(AuthenticatorTheme.materialColors.surfaceContainerLow)
                .padding(paddingValues)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Margin.Small)
        ) {
            accounts.forEach {
                AccountItem(it)
            }
        }
    }
}

@Composable
private fun AccountItem(account: FakeAccount) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .clickable(onClick = {}),
        colors = CardDefaults.cardColors(containerColor = AuthenticatorTheme.materialColors.surfaceBright),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = Margin.Small,
                horizontal = Margin.Medium,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Avatar de l'utilisateur",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AuthenticatorTheme.materialColors.surfaceContainerHighest)
            )
            Column(modifier = Modifier.padding(start = Margin.Medium)) {
                Text(text = account.name)
                Text(text = account.email)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(painterResource(R.drawable.right_arrow), null)
        }
    }
}

private data class FakeAccount(val name: String, val email: String, val isSafe: Boolean)

@PreviewSmallWindow
@Composable
fun HomeScreenPreview() {
    AuthenticatorTheme {
        HomeScreen()
    }
}
