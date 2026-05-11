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
package com.infomaniak.auth.ui.components.dialog

import android.annotation.SuppressLint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun VerifyAccountSecurityDialog(
    onChangePassword: () -> Unit,
    onContactSupport: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.triangle_alert), contentDescription = null)
        },
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(R.string.alertDialogVerifyAccountTitle),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(stringResource(R.string.alertDialogVerifyAccountText))
        },
        confirmButton = {
            TextButton(onClick = onChangePassword) {
                Text(stringResource(R.string.alertDialogChangePasswordButton))
            }
        },
        dismissButton = {
            TextButton(onClick = onContactSupport) {
                Text(stringResource(R.string.alertDialogContactSupportButton))
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@PreviewSmallWindow
@Composable
private fun VerifyAccountSecurityDialogPreview() {
    AuthenticatorTheme {
        Scaffold { _ ->
            VerifyAccountSecurityDialog(
                onChangePassword = {},
                onContactSupport = {},
            )
        }
    }
}
