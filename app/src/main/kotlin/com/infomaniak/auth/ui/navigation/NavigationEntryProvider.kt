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
package com.infomaniak.auth.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import com.infomaniak.auth.ui.dialog.disconnect.DisconnectConfirmDialog
import com.infomaniak.auth.ui.dialog.disconnect.DisconnectWarningDialog
import com.infomaniak.auth.ui.screen.accountdetails.AccountDetailsScreen
import com.infomaniak.auth.ui.screen.accountlist.AccountListScreen
import com.infomaniak.auth.ui.screen.home.HomeScreen
import com.infomaniak.auth.ui.screen.login.LoginScreen
import com.infomaniak.auth.ui.screen.onboarding.complete.OnboardingCompleteScreen
import com.infomaniak.auth.ui.screen.onboarding.migration.MigrationScreen
import com.infomaniak.auth.ui.screen.onboarding.start.OnboardingStartScreen
import com.infomaniak.auth.ui.screen.permission.NotificationPermissionScreen
import com.infomaniak.auth.ui.screen.securingaccount.SecuringAccountFromLoginInAppScreen
import com.infomaniak.auth.ui.screen.securingaccount.SecuringAccountFromOnboardingScreen
import com.infomaniak.auth.ui.screen.settings.SettingsScreen
import com.infomaniak.auth.ui.screen.settings.privacymanagement.PrivacyManagementMatomoScreen
import com.infomaniak.auth.ui.screen.settings.privacymanagement.PrivacyManagementScreen
import com.infomaniak.auth.ui.screen.settings.privacymanagement.PrivacyManagementSentryScreen
import com.infomaniak.auth.ui.screen.settings.theme.ThemeSettingsScreen
import com.infomaniak.core.privacymanagement.tracker.Tracker

fun baseEntryProvider(
    backStack: NavBackStack<NavKey>,
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<NavDestination.Home> {
        HomeScreen(rootBackStack = backStack)
    }
    entry<NavDestination.Theme> {
        ThemeSettingsScreen(onBackPressed = backStack::tryPopLast)
    }
    entry<NavDestination.PrivacyManagement> {
        PrivacyManagementScreen(
            navigateToTrackerPage = { tracker ->
                when (tracker) {
                    Tracker.Matomo -> {
                        backStack.add(NavDestination.PrivacyManagementMatomo)
                    }
                    Tracker.Sentry -> {
                        backStack.add(NavDestination.PrivacyManagementSentry)
                    }
                }
            },
            onBackPressed = backStack::tryPopLast
        )
    }
    entry<NavDestination.PrivacyManagementMatomo> {
        PrivacyManagementMatomoScreen(onBackPressed = backStack::tryPopLast)
    }
    entry<NavDestination.PrivacyManagementSentry> {
        PrivacyManagementSentryScreen(onBackPressed = backStack::tryPopLast)
    }
    entry<NavDestination.AccountDetails> {
        AccountDetailsScreen(
            accountId = it.accountId,
            onLoginPressed = { legacyAccount ->
                backStack.add(
                    NavDestination.LoginInApp.Form(legacyAccountId = legacyAccount, isOnboarding = false)
                )
            },
            onBackPressed = backStack::tryPopLast,
            onRemoveAccountClicked = { accountId, configuration ->
                backStack.add(
                    NavDestination.DisconnectDialog.DisconnectWarning(
                        accountId = accountId,
                        configuration = configuration
                    )
                )
            }
        )
    }
    entry<NavDestination.Onboarding.Migration> {
        MigrationScreen()
    }
    entry<NavDestination.Onboarding.Start> {
        OnboardingStartScreen()
    }
    entry<NavDestination.Onboarding.SecuringAccount> {
        SecuringAccountFromOnboardingScreen()
    }
    entry<NavDestination.Onboarding.Complete> {
        OnboardingCompleteScreen()
    }
    entry<NavDestination.Permission.Notification> {
        NotificationPermissionScreen(
            navigateToHome = {
                backStack.replaceAllWith(NavDestination.Home)
            },
        )
    }
    entry<NavDestination.LoginInApp.Form> {
        LoginScreen(
            legacyAccountId = it.legacyAccountId,
            onSendingCredentials = {
                backStack.add(
                    NavDestination.LoginInApp.SecuringAccount(
                        accountId = it.legacyAccountId,
                        isOnboarding = it.isOnboarding
                    )
                )
            },
            closeLoginScreen = backStack::tryPopLast,
            isOnboarding = it.isOnboarding
        )
    }
    entry<NavDestination.LoginInApp.SecuringAccount> {
        SecuringAccountFromLoginInAppScreen(
            accountId = it.accountId,
            onAccountLoggedIn = {
                if (!it.isOnboarding) {
                    backStack.popUntil(NavDestination.Home)
                }
            },
            returnToLoginScreen = backStack::tryPopLast,
        )
    }
    addDisconnectEntries(backStack)
}

private fun EntryProviderScope<NavKey>.addDisconnectEntries(backStack: NavBackStack<NavKey>) {
    entry<NavDestination.DisconnectDialog.DisconnectWarning>(
        metadata = DialogSceneStrategy.dialog()
    ) { params ->
        DisconnectWarningDialog(
            params.accountId,
            params.configuration,
            onDismissRequest = {
                backStack.clearDialog()
            },
            onConfirmButton = {
                backStack.clearDialog()
                backStack.add(
                    NavDestination.DisconnectDialog.DisconnectConfirmation(
                        accountId = params.accountId,
                        configuration =  params.configuration
                    )
                )
            }
        )
    }

    entry<NavDestination.DisconnectDialog.DisconnectConfirmation>(
        metadata = DialogSceneStrategy.dialog()
    ) { params ->
        DisconnectConfirmDialog(
            params.accountId,
            params.configuration,
            onAccountDisconnected = {
                backStack.popUntil(NavDestination.Home)
            },
            onDismissRequest = {
                backStack.clearDialog()
            }
        )
    }
}

fun homeEntryProvider(rootBackStack: NavBackStack<NavKey>): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<HomeSubDestination.AccountList> {
        AccountListScreen(
            onAccountClicked = { account ->
                rootBackStack.add(NavDestination.AccountDetails(account.id))
            },
        )
    }
    entry<HomeSubDestination.Settings> {
        SettingsScreen(
            onThemeClicked = {
                rootBackStack.add(NavDestination.Theme)
            },
            onPrivacyManagementClicked = {
                rootBackStack.add(NavDestination.PrivacyManagement)
            }
        )
    }
}

fun NavBackStack<NavKey>.popUntil(destination: NavKey) {
    while (lastIndex != 0 && this.last() != destination) {
        removeAt(lastIndex)
    }
}

fun NavBackStack<NavKey>.tryPopLast() {
    if (lastIndex == 0) return
    removeAt(lastIndex)
}

fun NavBackStack<NavKey>.clearDialog() {
    if (lastIndex == 0) return
    removeAll { it is NavDestination.DialogDestination }
}

fun NavBackStack<NavKey>.replaceAllWith(destination: NavKey) {
    add(destination)
    removeAll { it != destination }
}
