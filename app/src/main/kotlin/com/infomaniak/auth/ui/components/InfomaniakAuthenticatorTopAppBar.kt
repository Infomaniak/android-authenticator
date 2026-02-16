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

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.preview.PreviewLightAndDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfomaniakAuthenticatorTopAppBar(
    onBackPressed: (() -> Unit)? = null,
    withTitle: Boolean = true,
    isCentered: Boolean = true,
    isBackgroundTransparent: Boolean = false
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = if (isBackgroundTransparent) Color.Transparent else MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
    )
    val title: @Composable () -> Unit = {
        if (withTitle) {
            Text(
                text = stringResource(R.string.appCompleteName),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }

    val navigationIcon = @Composable {
        if (onBackPressed != null)
            IconButton(onClick = onBackPressed) {
                Icon(painterResource(R.drawable.arrow_left), contentDescription = null)
            }
    }

    if (isCentered) {
        CenterAlignedTopAppBar(
            navigationIcon = navigationIcon,
            title = title,
            colors = colors,
        )
    } else {
        TopAppBar(
            navigationIcon = navigationIcon,
            title = title,
            colors = colors,
        )
    }
}

@PreviewLightAndDark
@Composable
private fun InfomaniakAuthenticatorTopAppBarPreview() {
    AuthenticatorTheme {
        Surface {
            InfomaniakAuthenticatorTopAppBar()
        }
    }
}
