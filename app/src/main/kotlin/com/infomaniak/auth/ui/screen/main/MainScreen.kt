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
package com.infomaniak.auth.ui.screen.main

import android.Manifest
import android.os.Build.VERSION.SDK_INT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.AppStatus
import com.infomaniak.auth.lib.models.UrlConstants.HELP_SUPPORT_URL
import com.infomaniak.auth.lib.models.UrlConstants.RECOVER_PASSWORD_URL
import com.infomaniak.auth.ui.components.dialog.PasswordChangedDialog
import com.infomaniak.auth.ui.components.dialog.VerifyAccountSecurityDialog
import com.infomaniak.auth.ui.navigation.NavDestination
import com.infomaniak.auth.ui.navigation.baseEntryProvider
import com.infomaniak.auth.ui.navigation.replaceAllWith
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.auth.ui.theme.defaultEnterAnimation
import com.infomaniak.auth.ui.theme.defaultExitAnimation
import com.infomaniak.core.common.extensions.openUrlInCustomTab
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    startDestination: NavDestination,
    viewModel: MainViewModel
) {
    val backStack = rememberNavBackStack(startDestination)
    val currentDestination by remember(backStack) { derivedStateOf { backStack.last() } }
    val entryDecorators = persistentListOf<NavEntryDecorator<NavKey>>(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()
    )
    val notificationPermissionState: PermissionState? = if (SDK_INT >= 33) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null
    val hasTriggeredNotificationPermission by viewModel.hasTriggeredNotificationPermission.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.appStatus) {
        viewModel.appStatus.collect {
            val permissionStatus = notificationPermissionState?.status
            val shouldShowRationale = (permissionStatus as? PermissionStatus.Denied)?.shouldShowRationale == true
            val showNotificationPermissionScreen = notificationPermissionState != null &&
                    permissionStatus != PermissionStatus.Granted &&
                    (!hasTriggeredNotificationPermission || shouldShowRationale)

            handleAppStatus(
                appStatus = it,
                currentDestination = currentDestination,
                backStack = backStack,
                showNotificationPermissionScreen = showNotificationPermissionScreen,
            )
        }
    }

    var showPasswordChangedDialogFor: Account? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        viewModel.accountsWithPasswordUpdate.collect { accounts ->
            showPasswordChangedDialogFor = accounts.firstOrNull()
        }
    }
    showPasswordChangedDialogFor?.let { PasswordChangedStackDialog(account = it) }

    var showDisconnectedAccountDialogFor: Account? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        viewModel.accountsDisconnected.collect {  accounts ->
            showDisconnectedAccountDialogFor = accounts.firstOrNull()
        }
    }
    showDisconnectedAccountDialogFor?.let {  }

    MainScreen(backStack, entryDecorators)
}

@OptIn(ExperimentalPermissionsApi::class)
private fun handleAppStatus(
    appStatus: AppStatus,
    currentDestination: NavKey,
    backStack: NavBackStack<NavKey>,
    showNotificationPermissionScreen: Boolean,
) {
    val targetDestination = when (appStatus) {
        is AppStatus.LoginRequired.NotMigrating -> NavDestination.Onboarding.Start
        is AppStatus.LoginRequired.MigratingFromLegacyKAuth -> NavDestination.Onboarding.Migration
        is AppStatus.LoginRequired.MustReLogin -> NavDestination.LoginInApp.Form(legacyAccountId = appStatus.accountId, isOnboarding = true)
        is AppStatus.LoggingIn -> NavDestination.Onboarding.SecuringAccount
        is AppStatus.EverythingReady -> NavDestination.Onboarding.Complete
        is AppStatus.SetupComplete -> {
            if (showNotificationPermissionScreen) {
                NavDestination.Permission.Notification
            } else {
                NavDestination.Home
            }
        }
        is AppStatus.AddingAnAccount -> NavDestination.Onboarding.Start
    }

    if (currentDestination != targetDestination) {
        backStack.replaceAllWith(targetDestination)
    }
}

@Composable
fun MainScreen(
    backStack: NavBackStack<NavKey>,
    entryDecorators: ImmutableList<NavEntryDecorator<NavKey>>,
) {
    NavDisplay(
        backStack = backStack,
        entryDecorators = entryDecorators,
        entryProvider = baseEntryProvider(backStack),
        transitionSpec = { defaultEnterAnimation },
        popTransitionSpec = { defaultExitAnimation },
        predictivePopTransitionSpec = { defaultExitAnimation },
    )
}

@Composable
private fun PasswordChangedStackDialog(
    account: Account,
) {
    var showPasswordChangedDialog by remember { mutableStateOf(true) }
    LaunchedEffect(account) {
        showPasswordChangedDialog = true
    }

    var showVerifyAccountDialog by remember { mutableStateOf(false) }

    if (showPasswordChangedDialog) {
        PasswordChangedDialog(
            account = account,
            onDismissButton = {
                (account.status as? Account.Status.LoggedIn)?.passwordChangedAck?.invoke()
                showPasswordChangedDialog = false
            },
            onReportUnauthorizedChange = {
                showPasswordChangedDialog = false
                showVerifyAccountDialog = true
            }
        )
    }

    if (showVerifyAccountDialog) {
        val context = LocalContext.current

        VerifyAccountSecurityDialog(
            onChangePassword = {
                (account.status as? Account.Status.LoggedIn)?.passwordChangedAck?.invoke()
                context.openUrlInCustomTab(RECOVER_PASSWORD_URL)
                showVerifyAccountDialog = false
                showPasswordChangedDialog = true
            },
            onContactSupport = {
                (account.status as? Account.Status.LoggedIn)?.passwordChangedAck?.invoke()
                context.openUrlInCustomTab(HELP_SUPPORT_URL)
                showVerifyAccountDialog = false
                showPasswordChangedDialog = true
            },
        )
    }
}

@PreviewSmallWindow
@Composable
private fun MainScreenPreview() {
    AuthenticatorTheme {
        val owner = object : NavigationEventDispatcherOwner {
            override val navigationEventDispatcher: NavigationEventDispatcher = NavigationEventDispatcher()
        }
        CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides owner) {
            val backStack = rememberNavBackStack(NavDestination.Home)
            val entryDecorators = persistentListOf<NavEntryDecorator<NavKey>>()
            MainScreen(backStack, entryDecorators)
        }
    }
}
