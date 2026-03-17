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
package com.infomaniak.auth.lib

sealed interface AppStatus {

    /**
     * When [isMigratingFromLegacyKAuth] is true, look for [AuthenticatorFacade.accounts] to get the list
     * of accounts that are pending migration.
     */
    data class LoginRequired(
        val isMigratingFromLegacyKAuth: Boolean,
        val proceed: (() -> Unit)?,
    ) : AppStatus

    data class LoggingIn(val pendingAction: NotConnectedAction?) : AppStatus

    /**
     * Calling [proceed] will lead [AuthenticatorFacade.appStatus] to switch to [SetupComplete].
     */
    data class OnboardingDone(val proceed: () -> Unit) : AppStatus

    data object SetupComplete : AppStatus
}
