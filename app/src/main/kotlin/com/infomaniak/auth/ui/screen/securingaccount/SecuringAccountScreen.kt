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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.infomaniak.auth.R
import com.infomaniak.multiplatform_authenticator.core.Account
import com.infomaniak.multiplatform_authenticator.core.matomo.MatomoScreen
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.personKAuthAuthenticatorHalfCircle.PersonKAuthAuthenticatorHalfCircle
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.auth.ui.theme.LocalWindowAdaptiveInfo
import com.infomaniak.auth.utils.MatomoTrackScreen
import com.infomaniak.auth.utils.isWindowSmall
import com.infomaniak.core.ui.compose.basics.LockScreenOrientation
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun SecuringAccountFromLoginInAppScreen(
    accountId: Long,
    onAccountLoggedIn: () -> Unit,
    returnToLoginScreen: () -> Unit,
    viewModel: SecuringAccountViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        handleLoginResultWithMinDelay(
            fetchAccountStatus = viewModel.fetchAccountStatus(accountId),
            onAccountLoggedIn = onAccountLoggedIn,
            returnToLoginScreen = returnToLoginScreen
        )
    }

    LockScreenOrientation(isLocked = LocalWindowAdaptiveInfo.current.isWindowSmall())

    SecuringAccountScreen()
}

private suspend fun handleLoginResultWithMinDelay(
    fetchAccountStatus: Flow<Account.Status>,
    onAccountLoggedIn: () -> Unit,
    returnToLoginScreen: () -> Unit
) = coroutineScope {
    launch {
        coroutineScope {
            launch { delay(2.seconds) } // Keep this screen visible long enough to avoid a flash before navigating to the success state.
            fetchAccountStatus.first { it is Account.Status.LoggedIn }
        }
        onAccountLoggedIn()
    }
    launch {
        coroutineScope {
            launch { delay(.7.seconds) } // Keep this screen visible long enough to avoid a flash before returning to the login screen.
            fetchAccountStatus.first { it is Account.Status.NotConnected.ReLogin && (it.lastIssue != null || it.hadIncorrectPassword) }
        }
        returnToLoginScreen()
    }
}

@Composable
fun SecuringAccountFromOnboardingScreen() {
    SecuringAccountScreen()
}

@Composable
private fun SecuringAccountScreen(modifier: Modifier = Modifier) {
    MatomoTrackScreen(MatomoScreen.SecuringAccountScreen)

    SinglePaneScaffold(
        modifier = modifier,
        topBar = {
            InfomaniakAuthenticatorTopAppBar()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            IllustrationWithHalo(
                themedImage = AppImages.AppIllus.PersonKAuthAuthenticatorHalfCircle,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Text(text = stringResource(R.string.onBoardingSecuringAccount))
            Spacer(modifier = Modifier.padding(Margin.Medium))
            LinearProgressIndicator()
        }
    }
}

@PreviewSmallWindow
@Composable
private fun SecuringAccountScreenPreview() {
    AuthenticatorTheme {
        SecuringAccountScreen()
    }
}
