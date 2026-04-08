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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.ButtonStyle
import com.infomaniak.auth.ui.components.EmptyElement
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.gridTilesWithAuthenticator.GridTilesWithAuthenticator
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.BottomStickyButtonScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewLightAndDark
import com.infomaniak.core.common.R as RCore

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
) {
    LoginScreen(email = { "" })
}

@Composable
private fun LoginScreen(
    email: () -> String,
    modifier: Modifier = Modifier,
) {
    BottomStickyButtonScaffold(
        modifier = modifier,
        topBar = {
            InfomaniakAuthenticatorTopAppBar()
        },
        bottomButton = { bottomModifier ->
            BottomButton(
                modifier = bottomModifier
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(Margin.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Margin.Medium),
        ) {
            EmptyElement()
            IllustrationWithHalo(AppImages.AppIllus.GridTilesWithAuthenticator)
            Text(
                text = stringResource(R.string.onBoardingLoginTitle),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium)
            )
            LoginForm()
        }
    }
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Margin.Medium),
    ) {
        OutlinedTextField(
            state = rememberTextFieldState("Hello World Invisible"),
            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 1),
            label = { Text(stringResource(R.string.emailLabel)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            state = rememberTextFieldState("Hello World Invisible"),
            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 1),
            label = { Text(stringResource(R.string.passwordLabel)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BottomButton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Margin.Medium)
    ) {
        LargeButton(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.logInButton),
            onClick = {  }
        )
        LargeButton(
            modifier = Modifier.fillMaxWidth(),
            style = ButtonStyle.Tertiary,
            title = stringResource(RCore.string.buttonCancel),
            onClick = {  }
        )
    }
}

@PreviewLightAndDark
@Composable
private fun LoginScreenPreview() {
    AuthenticatorTheme {
        LoginScreen(
            email = { "auth@infomaniak.com" },
        )
    }
}
