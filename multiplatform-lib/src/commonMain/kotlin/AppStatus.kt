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

    sealed interface Onboarding : AppStatus {

        /**
         * When in this state, look for [AuthenticatorFacade.accounts] to get the list
         * of accounts that are pending migration.
         */
        data class NeedsMigration(val startMigrating: () -> Unit) : Onboarding

        data class Migrating(val pendingAction: PendingUserAction?) : Onboarding {
            sealed interface PendingUserAction {

                data class ReLogin(
                    val legacyAccount: Account,
                    val sendCredentials: (CredentialsForMigration) -> Unit,
                ) : PendingUserAction

                data class Issue(
                    val shouldRetry: (Boolean) -> Unit,
                ) : PendingUserAction
            }
        }

        data object BrandNew : Onboarding
    }

    data object SetupComplete : AppStatus
}
