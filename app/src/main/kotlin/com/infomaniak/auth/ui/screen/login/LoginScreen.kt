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
package com.infomaniak.auth.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.CredentialsForMigration
import com.infomaniak.auth.lib.NotConnectedAction
import com.infomaniak.auth.lib.models.UrlConstants.RECOVER_PASSWORD_URL
import com.infomaniak.auth.ui.components.AccountRow
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.components.OpenUrlButton
import com.infomaniak.auth.ui.previewparameter.fakeAccounts
import com.infomaniak.auth.ui.theme.AppDimens.DefaultCornerRadius
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.basics.Typography
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.BottomStickyButtonScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewLightAndDark

@Composable
fun LoginScreen(
    legacyAccountId: Long,
    onBackPressed: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by remember(legacyAccountId) {
        viewModel.uiStateForAccount(legacyAccountId)
    }.collectAsState(LoginUiState.Loading)

    when (val state = uiState) {
        is LoginUiState.Loading -> Unit
        is LoginUiState.Ready -> {
            LoginScreen(
                legacyAccount = { state.legacyAccount },
                onBackPressed = onBackPressed,
                onLoginPressed = { email, password ->
                    val status = state.legacyAccount.status as? Account.Status.NotConnected
                    val action = status?.action as? NotConnectedAction.ReLogin
                    if (password.isNotEmpty()) {
                        action?.sendCredentials(CredentialsForMigration(email, password))
                    }
                }
            )
        }
    }
}

@Composable
private fun LoginScreen(
    legacyAccount: () -> Account,
    onBackPressed: () -> Unit,
    onLoginPressed: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val passwordState = rememberTextFieldState(initialText = "")

    BottomStickyButtonScaffold(
        modifier = modifier.imePadding(),
        topBar = {
            InfomaniakAuthenticatorTopAppBar(
                withTitle = false,
                isCentered = true,
                isBackgroundTransparent = true,
                onBackPressed = onBackPressed
            )
        },
        topButton = { topModifier ->
            LargeButton(
                modifier = topModifier.fillMaxWidth(),
                title = stringResource(R.string.logInButton),
                onClick = {
                    onLoginPressed(legacyAccount().email, passwordState.text.toString())
                }
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Margin.Mini)) {
            Column(
                modifier = Modifier.padding(horizontal = Margin.Medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Margin.Medium),
            ) {
                Text(stringResource(R.string.logInTitle), style = Typography.h1)
                Text(
                    text = stringResource(R.string.logInDescription),
                    style = MaterialTheme.typography.bodyLarge
                )
                LoginForm(passwordState, legacyAccount)
            }
            OpenUrlButton(
                text = stringResource(R.string.passwordForgottenButton),
                sourceUrl = RECOVER_PASSWORD_URL,
                leadingIcon = {
                    Icon(
                        modifier = Modifier.padding(end = Margin.Mini),
                        painter = painterResource(R.drawable.ic_circle_information_full), contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun LoginForm(
    passwordState: TextFieldState,
    legacyAccount: () -> Account,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Margin.Medium),
    ) {
        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AuthenticatorTheme.customColors.sectionBackground),
            shape = RoundedCornerShape(DefaultCornerRadius),
        ) {
            AccountRow(
                modifier = Modifier.padding(Margin.Medium),
                account = legacyAccount()
            )
            HorizontalDivider(
                color = AuthenticatorTheme.materialColors.outlineVariant,
            )
            SecureTextField(
                state = passwordState,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                ),
                label = { Text(stringResource(R.string.passwordLabel)) },
                textObfuscationMode = if (passwordVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(painterResource(R.drawable.ic_eye_crossed), contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Margin.Micro),
            )
        }
    }
}

@PreviewLightAndDark
@Composable
private fun LoginScreenPreview() {
    AuthenticatorTheme {
        LoginScreen(
            legacyAccount = { fakeAccounts.first() },
            onBackPressed = {},
            onLoginPressed = { _, _ -> },
        )
    }
}
