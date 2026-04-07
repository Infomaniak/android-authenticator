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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.theme.AppDimens.DefaultCornerRadius
import com.infomaniak.auth.ui.theme.AuthenticatorTheme

@Composable
fun AuthenticatorFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = AuthenticatorTheme.materialColors.primaryContainer,
        shape = RoundedCornerShape(DefaultCornerRadius),
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = stringResource(R.string.addAccountContentDescription),
        )
    }
}

@Preview
@Composable
private fun AuthenticatorFabPreview() {
    AuthenticatorTheme {
        AuthenticatorFab(onClick = {})
    }
}
