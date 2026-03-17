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

    sealed interface LoginRequired : AppStatus {

        /**
         * The list of accounts that are pending migration can be found in [AuthenticatorFacade.accounts].
         */
        data class MigratingFromLegacyKAuth(val proceed: () -> Unit) : LoginRequired

        /**
         * [AuthenticatorFacade.appStatus] will automatically change to
         * [LoggingIn] after [AuthenticatorFacade.addAccounts] is called.
         */
        data object NotMigrating : LoginRequired
    }

    /**
     * This status is emitted by [AuthenticatorFacade.appStatus] once [AuthenticatorFacade.addAccounts] is called.
     *
     * After the login completes successfully, [AuthenticatorFacade.appStatus] will switch to [OnboardingDone].
     *
     * If [needsResolution] is true, the [Account.status] property from the accounts in [AuthenticatorFacade.accounts]
     * need to be checked, and addressed.
     */
    data class LoggingIn(val needsResolution: Boolean) : AppStatus

    /**
     * Calling [proceed] will lead [AuthenticatorFacade.appStatus] to switch to [SetupComplete].
     */
    data class OnboardingDone(val proceed: () -> Unit) : AppStatus

    data object SetupComplete : AppStatus
}
