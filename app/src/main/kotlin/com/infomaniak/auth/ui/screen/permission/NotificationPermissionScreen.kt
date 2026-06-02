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
package com.infomaniak.auth.ui.screen.permission

import android.Manifest
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.infomaniak.auth.MainApplication
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.matomo.MatomoScreen
import com.infomaniak.auth.ui.components.ButtonStyle
import com.infomaniak.auth.ui.components.EmptyElement
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.components.TitleAndDescription
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.bannerNotification.BannerNotification
import com.infomaniak.auth.ui.screen.main.MainViewModel
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.auth.ui.theme.LocalWindowAdaptiveInfo
import com.infomaniak.auth.utils.MatomoTrackScreen
import com.infomaniak.auth.utils.isWindowSmall
import com.infomaniak.core.ui.compose.basics.LockScreenOrientation
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.BottomStickyButtonScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewLightAndDark

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionScreen(
    navigateToHome: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val hasTriggeredNotificationPermission by viewModel.hasTriggeredNotificationPermission.collectAsStateWithLifecycle()

    val notificationPermissionState: PermissionState? = if (SDK_INT >= 33) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null

    var permissionAsked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(notificationPermissionState?.status) {
        if (
            notificationPermissionState == null ||
            notificationPermissionState.status == PermissionStatus.Granted ||
            notificationPermissionState.status is PermissionStatus.Denied && permissionAsked
        ) {
            if (!hasTriggeredNotificationPermission) {
                viewModel.onNotificationPermissionTriggered()
            }
            navigateToHome()
            (context.applicationContext as? MainApplication)?.registerUserDeviceIfNeeded()
        }
    }

    MatomoTrackScreen(MatomoScreen.NotificationPermissionScreen)

    NotificationPermissionScreen(
        navigateToHome = navigateToHome,
        onPermissionAsked = {
            notificationPermissionState?.launchPermissionRequest()
            permissionAsked = true
        },
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationPermissionScreen(
    navigateToHome: () -> Unit,
    onPermissionAsked: () -> Unit,
) {
    LockScreenOrientation(isLocked = LocalWindowAdaptiveInfo.current.isWindowSmall())

    BottomStickyButtonScaffold(
        topBar = {
            InfomaniakAuthenticatorTopAppBar()
        },
        bottomButton = { modifier ->
            Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Margin.Medium)) {
                LargeButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.onboardingNotificationsAuthorisationButton),
                    onClick = onPermissionAsked
                )
                LargeButton(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.laterButton),
                    style = ButtonStyle.Tertiary,
                    onClick = navigateToHome
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
            IllustrationWithHalo(
                themedImage = AppImages.AppIllus.BannerNotification,
                modifier = Modifier.fillMaxSize(0.8f)
            )
            TitleAndDescription(
                title = stringResource(R.string.onboardingNotificationsTitle),
                description = stringResource(R.string.onboardingNotificationsDescription)
            )
        }
    }
}


@PreviewLightAndDark
@Composable
private fun NotificationPermissionScreenPreview() {
    AuthenticatorTheme {
        NotificationPermissionScreen(
            navigateToHome = {},
            onPermissionAsked = {},
        )
    }
}
