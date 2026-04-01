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
import com.infomaniak.auth.ui.screen.accountdetails.AccountDetailsScreen
import com.infomaniak.auth.ui.screen.home.HomeScreen
import com.infomaniak.auth.ui.screen.onboarding.complete.OnboardingCompleteScreen
import com.infomaniak.auth.ui.screen.onboarding.migration.MigrationScreen
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
            onAccountClicked = { account ->
                backStack.add(NavDestination.AccountDetails(account.id))
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
            onBackPressed = backStack::tryPopLast
        )
    }
    entry<NavDestination.Onboarding.Migration> {
        MigrationScreen(onContinue = it.onContinue)
    }
    entry<NavDestination.Onboarding.Start> {
        OnboardingStartScreen(
            snackbarHostState = snackbarHostState,
            onCreateAccount = {}
        )
    }
    entry<NavDestination.SecuringAccount> {
        SecuringAccountScreen()
    }
    entry<NavDestination.Onboarding.Complete> {
        OnboardingCompleteScreen(onContinue = it.onContinue)
    }
}

private fun NavBackStack<NavKey>.tryPopLast() {
    if (lastIndex == 0) return
    removeAt(lastIndex)
}
