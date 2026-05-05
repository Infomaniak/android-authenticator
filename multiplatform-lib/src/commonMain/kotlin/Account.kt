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

data class Account(
    val id: Long,
    val fullName: String,
    val initials: String,
    val email: String,
    val avatarUrl: String? = null,
    val status: Status,
) {
    sealed interface Status {

        /**
         * @property securityScore can range from 0 to 5
         * @property passwordChangedAck is set when the password changed. Call it to acknowledge the change and dismiss it.
         */
        data class LoggedIn(
            val securityScore: Int? = null,
            val passwordChangedAck: (() -> Unit)? = null,
        ) : Status

        sealed interface NotConnected : Status {

            data object AttemptingToConnect : NotConnected

            /**
             * The actual email of the account might have changed since then, and we can't know about it.
             * So, the UI is supposed to pre-fill it with the one in [legacyAccount], and prompt the user to check it's
             * correct, letting them replace it if needed (editable text field).
             */
            data class ReLogin(
                val legacyAccount: Account,
                val hadIncorrectPassword: Boolean = false,
                val lastIssue: DismissableIssue?,
                val sendCredentials: ((CredentialsForMigration) -> Unit)?,
            ) : NotConnected {

                data class DismissableIssue(
                    val dismiss: () -> Unit,
                    val cause: Issue.Retriable.Cause,
                )

                val isSendingCredentials: Boolean get() = sendCredentials == null
            }

            data class LoginFailed(val issue: Issue) : NotConnected
        }

        data class PasswordChanged(val hasBeenHandled: () -> Unit) : Status
    }
}
