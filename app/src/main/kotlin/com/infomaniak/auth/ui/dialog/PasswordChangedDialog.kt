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
package com.infomaniak.auth.ui.dialog

import android.annotation.SuppressLint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.ui.previewparameter.fakeAccounts
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun PasswordChangedDialog(
    account: Account,
    onDismissButton: () -> Unit,
    onReportUnauthorizedChange: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(R.drawable.alert), contentDescription = null)
        },
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(R.string.alertDialogPasswordChangedTitle),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(stringResource(R.string.alertDialogPasswordChangedText, account.email))
        },
        confirmButton = {
            TextButton(
                onClick = onReportUnauthorizedChange,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                )
            ) {
                Text(stringResource(R.string.alertDialogReportButton))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissButton) {
                Text(stringResource(R.string.alertDialogNeutralButton))
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@PreviewSmallWindow
@Composable
private fun PasswordChangedDialogPreview() {
    AuthenticatorTheme {
        Scaffold { _ ->
            PasswordChangedDialog(
                account = fakeAccounts.first(),
                onDismissButton = {},
                onReportUnauthorizedChange = {}
            )
        }
    }
}
