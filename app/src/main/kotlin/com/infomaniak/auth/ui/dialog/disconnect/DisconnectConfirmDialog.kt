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
package com.infomaniak.auth.ui.dialog.disconnect

import android.annotation.SuppressLint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.screen.accountdetails.DisconnectConfiguration
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun DisconnectConfirmDialog(
    accountId: Long,
    configuration: DisconnectConfiguration,
    onAccountDisconnected: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: DisconnectDialogViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchAccountDetails(accountId)
    }

    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(R.drawable.triangle_alert),
                contentDescription = null
            )
        },
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(configuration.confirmationTitleResId),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(text = stringResource(configuration.confirmationDescriptionResId))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.removeAccount(onAccountRemoved = onAccountDisconnected)
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                )
            ) {
                Text(stringResource(configuration.criticalButtonStringResId))
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@PreviewSmallWindow
@Composable
private fun DisconnectWarningDialogPreview() {
    AuthenticatorTheme {
        Scaffold { _ ->
            DisconnectConfirmDialog(
                accountId = 1L,
                configuration = DisconnectConfiguration.DisconnectSecuredAccount,
                onAccountDisconnected = {},
                onDismissRequest = {},
            )
        }
    }
}
