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
package com.infomaniak.auth.ui.screen.onboarding.permission

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.ButtonStyle
import com.infomaniak.auth.ui.components.EmptyElement
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.components.TitleAndDescription
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.bannerNotification.BannerNotification
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.BottomStickyButtonScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@SuppressLint("ComposeModifierMissing")
@Composable
fun NotificationPermissionScreen() {
    BottomStickyButtonScaffold(
        topBar = {
            InfomaniakAuthenticatorTopAppBar()
        },
        bottomButton = { modifier ->
            Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Margin.Medium)) {
                LargeButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.onboardingNotificationsAuthorisationButton),
                    onClick = { }
                )
                LargeButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.laterButton),
                    style = ButtonStyle.Tertiary,
                    onClick = { }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            EmptyElement()
            IllustrationWithHalo(AppImages.AppIllus.BannerNotification)
            TitleAndDescription(
                title = stringResource(R.string.onboardingNotificationsTitle),
                description = stringResource(R.string.onboardingNotificationsDescription)
            )
        }
    }
}


@PreviewSmallWindow
@Composable
private fun NotificationPermissionScreenPreview() {
    AuthenticatorTheme {
        NotificationPermissionScreen()
    }
}
