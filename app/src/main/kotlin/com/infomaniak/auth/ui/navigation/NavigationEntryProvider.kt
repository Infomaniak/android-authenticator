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

import androidx.compose.material3.SnackbarHostState
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.infomaniak.auth.ui.screen.accountdetails.AccountDetails
import com.infomaniak.auth.ui.screen.home.HomeScreen
import com.infomaniak.auth.ui.screen.onboarding.complete.OnboardingCompleteScreen
import com.infomaniak.auth.ui.screen.onboarding.start.OnboardingStartScreen
import com.infomaniak.auth.ui.screen.securingaccount.SecuringAccountScreen
import com.infomaniak.auth.ui.screen.settings.SettingsScreen
import com.infomaniak.auth.ui.screen.settings.privacymanagement.PrivacyManagementMatomoScreen
import com.infomaniak.auth.ui.screen.settings.privacymanagement.PrivacyManagementScreen
import com.infomaniak.auth.ui.screen.settings.privacymanagement.PrivacyManagementSentryScreen
import com.infomaniak.auth.ui.screen.settings.theme.ThemeSettingsScreen
import com.infomaniak.core.privacymanagement.tracker.Tracker

fun baseEntryProvider(
    backStack: NavBackStack<NavKey>,
    snackbarHostState: SnackbarHostState,
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<NavDestination.Root.Home> {
        HomeScreen(
            onAccountClicked = {
                backStack.add(NavDestination.AccountDetails(it))
            }
        )
    }
    entry<NavDestination.Root.Settings> {
        SettingsScreen(
            onThemeClicked = {
                backStack.add(NavDestination.Theme)
            },
            onPrivacyManagementClicked = {
                backStack.add(NavDestination.PrivacyManagement)
            }
        )
    }
    entry<NavDestination.Theme> {
        ThemeSettingsScreen(onBackPressed = backStack::popLast)
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
            onBackPressed = backStack::popLast
        )
    }
    entry<NavDestination.PrivacyManagementMatomo> {
        PrivacyManagementMatomoScreen(onBackPressed = backStack::popLast)
    }
    entry<NavDestination.PrivacyManagementSentry> {
        PrivacyManagementSentryScreen(onBackPressed = backStack::popLast)
    }
    entry<NavDestination.AccountDetails> {
        AccountDetails(
            it.account,
            onBackPressed = backStack::popLast
        )
    }
    entry<NavDestination.Onboarding.Start> {
        OnboardingStartScreen(
            snackbarHostState = snackbarHostState,
            onLoginFinished = { backStack.add(NavDestination.SecuringAccount) },
            onCreateAccount = {}
        )
    }
    entry<NavDestination.SecuringAccount> {
        SecuringAccountScreen(
            onFinish = { backStack.add(NavDestination.Onboarding.Complete) }
        )
    }
    entry<NavDestination.Onboarding.Complete> {
        OnboardingCompleteScreen(
            navigateToHome = {
                backStack.clear()
                backStack.add(NavDestination.Root.Home)
            }
        )
    }
}

private fun NavBackStack<NavKey>.popLast() {
    removeAt(lastIndex)
}
