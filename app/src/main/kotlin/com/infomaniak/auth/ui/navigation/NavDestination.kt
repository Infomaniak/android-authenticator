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

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Immutable
@Serializable
sealed interface NavDestination : NavKey {
    //region Onboarding

    sealed interface Onboarding : NavDestination {
        @Serializable
        data object Migration : Onboarding

        @Serializable
        data object Start : Onboarding

        @Serializable
        data object SecuringAccount : Onboarding

        @Serializable
        data object Complete : Onboarding
    }

    sealed interface Permission : NavDestination {
        @Serializable
        data object Notification : Permission
    }

    @Serializable
    data class SecuringAccount(val accountId: Long) : NavDestination

    //endregion

    //region Home

    @Serializable
    data object Home : NavDestination

    //endregion

    //region Settings

    @Serializable
    data object Theme : NavDestination

    @Serializable
    data object PrivacyManagement : NavDestination

    @Serializable
    data object PrivacyManagementMatomo : NavDestination

    @Serializable
    data object PrivacyManagementSentry : NavDestination

    //endregion

    @Serializable
    data class LoginInApp(val legacyAccountId: Long, val isOnboarding: Boolean) : NavDestination

    @Serializable
    data class AccountDetails(val accountId: Long) : NavDestination
}

@Immutable
@Serializable
sealed interface HomeSubDestination : NavKey {
    @Serializable
    data object AccountList : HomeSubDestination

    @Serializable
    data object Settings : HomeSubDestination
}
