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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.ButtonStyle
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.basics.Typography
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.BottomStickyButtonScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewLightAndDark
import com.infomaniak.core.common.R as RCore

@Composable
fun LoginScreen(
    legacyAccountId: Long,
    onBackPressed: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val legacyAccount by viewModel.legacyAccount.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchLegacyAccount(legacyAccountId)
    }

    LoginScreen(
        legacyEmail = { legacyAccount?.email ?: "" },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun LoginScreen(
    legacyEmail: () -> String,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomStickyButtonScaffold(
        modifier = modifier,
        topBar = {
            InfomaniakAuthenticatorTopAppBar(
                withTitle = false,
                isCentered = false,
                isBackgroundTransparent = true,
                onBackPressed = onBackPressed
            )
        },
        topButton = { topModifier ->
            LargeButton(
                modifier = topModifier.fillMaxWidth(),
                title = stringResource(R.string.logInButton),
                onClick = {  }
            )
        },
        bottomButton = { bottomModifier ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LargeButton(
                    modifier = bottomModifier.fillMaxWidth(),
                    style = ButtonStyle.Tertiary,
                    title = stringResource(RCore.string.buttonCancel),
                    onClick = onBackPressed
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(Margin.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Margin.Medium),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Margin.Medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.logInButton), style = Typography.h1)
            }
            Text(
                text = stringResource(R.string.onBoardingLoginTitle),
                // textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            LoginForm(legacyEmail)
            Spacer(modifier = Modifier.weight(1f))
            // Image(
            //     painter = painterResource(R.drawable.),
            //     colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            //     contentDescription = null
            // )
            // IllustrationWithHalo(AppImages.AppIllus.GridTilesWithAuthenticator)
        }
    }
}

@Composable
private fun LoginForm(
    legacyEmail: () -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Margin.Medium),
    ) {
        val emailInputState = rememberTextFieldState(legacyEmail())
        val passwordInputState = rememberTextFieldState("")
        OutlinedTextField(
            state = emailInputState,
            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 1),
            label = { Text(stringResource(R.string.emailLabel)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            state = passwordInputState,
            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 1),
            label = { Text(stringResource(R.string.passwordLabel)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewLightAndDark
@Composable
private fun LoginScreenPreview() {
    AuthenticatorTheme {
        LoginScreen(
            legacyEmail = { "auth@infomaniak.com" },
            onBackPressed = {},
        )
    }
}
