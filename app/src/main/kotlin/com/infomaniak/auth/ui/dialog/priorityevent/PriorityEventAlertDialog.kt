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
package com.infomaniak.auth.ui.dialog.priorityevent

import android.annotation.SuppressLint
import androidx.annotation.StringRes
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
import com.infomaniak.multiplatform_authenticator.core.Account
import com.infomaniak.auth.ui.previewparameter.fakeAccounts
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun PriorityEventAlertDialog(
    account: Account,
    event: PriorityEventAlert,
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
                text = stringResource(event.titleResId),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(text = stringResource(event.descriptionResId, account.email))
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

enum class PriorityEventAlert(
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int
) {
    PasswordChanged(
        titleResId = R.string.alertDialogPasswordChangedTitle,
        descriptionResId = R.string.alertDialogPasswordChangedText
    ),
    AccountDisconnected(
        titleResId = R.string.accountDisconnectedTitle,
        descriptionResId = R.string.accountDisconnectedDescription
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@PreviewSmallWindow
@Composable
private fun PriorityEventAlertDialogPreview() {
    AuthenticatorTheme {
        Scaffold { _ ->
            PriorityEventAlertDialog(
                account = fakeAccounts.first(),
                event = PriorityEventAlert.PasswordChanged,
                onDismissButton = {},
                onReportUnauthorizedChange = {}
            )
        }
    }
}
