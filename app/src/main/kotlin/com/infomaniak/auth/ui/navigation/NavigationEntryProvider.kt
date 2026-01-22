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

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.infomaniak.auth.ui.screen.home.HomeScreen
import com.infomaniak.auth.ui.screen.onboarding.OnboardingScreen
import com.infomaniak.auth.ui.screen.ready.ReadyScreen
import com.infomaniak.auth.ui.screen.securingaccount.SecuringAccountScreen

fun baseEntryProvider(backStack: NavBackStack<NavKey>): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<NavDestination.Home> {
        HomeScreen()
    }
    entry<NavDestination.Onboarding> {
        OnboardingScreen(
            onLogin = { backStack.add(NavDestination.SecuringAccount) },
            onCreateAccount = {}
        )
    }
    entry<NavDestination.SecuringAccount> {
        SecuringAccountScreen(
            onFinish = { backStack.add(NavDestination.Ready) }
        )
    }
    entry<NavDestination.Ready> {
        ReadyScreen(
            navigateToHome = {
                backStack.clear()
                backStack.add(NavDestination.Home)
            }
        )
    }
}
