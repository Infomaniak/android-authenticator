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
package com.infomaniak.auth.ui.screen.securingaccount

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.infomaniakValidated.InfomaniakValidated
import com.infomaniak.auth.ui.images.illus.personKAuthAuthenticator.PersonKAuthAuthenticator
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun SecuringAccountScreen(
    viewModel: SecuringAccountViewModel = viewModel(),
    onFinish: () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.doMagicThing(onFinish)
    }

    SinglePaneScaffold(
        topBar = {
            InfomaniakAuthenticatorTopAppBar()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                // TODO[ik-auth]: Remove this when magic thing become not magic
                .clickable(onClick = onFinish),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            IllustrationWithHalo(AppImages.AppIllus.PersonKAuthAuthenticator)
            Text(
                text = stringResource(R.string.onBoardingSecuringAccount)
            )
            Spacer(modifier = Modifier.padding(Margin.Medium))
            LinearProgressIndicator()
        }
    }
}

@PreviewSmallWindow
@Composable
fun SecuringAccountScreenPreview() {
    AuthenticatorTheme {
        SecuringAccountScreen(onFinish = { })
    }
}
