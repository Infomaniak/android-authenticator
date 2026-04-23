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

sealed interface Issue {

    /**
     * When coming from kAuth, if at least one account was successfully migrated, we don't have this at all.
     * @property proceed Typically called when the user presses a button labeled "Skip" or "Retry".
     */
    data class Retriable(
        val reason: Reason,
        val proceed: (shouldRetry: Boolean) -> Unit
    ) : Issue {
        sealed interface Reason {
            data object NetworkIssue : Reason
            data object ServerUnavailable : Reason
            data class Other(val errorCode: Int, val message: String) : Reason
        }
    }

    /** Should never happen, since it's linked to normally impossible app-internal cases. */
    data class NonRetriable(val message: String) : Issue

}
