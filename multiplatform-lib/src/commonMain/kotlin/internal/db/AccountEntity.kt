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
package com.infomaniak.auth.lib.internal.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.infomaniak.auth.lib.AuthenticatorFacade

@Entity
internal data class AccountEntity(
    @PrimaryKey val id: Long = 0,
    val fullName: String,
    val initials: String,
    val email: String,
    val avatarUrl: String? = null,
    val status: Status,
) {
    val isLoggedIn: Boolean get() = status == Status.LoggedIn

    enum class Status {

        /*
        WARNING to editors:
        Since the enum ordinal (index) is used in the DB, what it represents must never change.
        In particular:
        1. NEVER CHANGE THE ORDER of the enum entries
        2. NEVER REMOVE AN ENTRY (deprecation and renaming are okay)
        3. As a result, NEW ENTRIES must always be added AT THE END.
         */

        /** Account from kAuth that has not yet been migrated. */
        ToBeMigrated,

        /** Account added via [AuthenticatorFacade.addAccounts], with passkey registration not complete yet. */
        PasskeyRegistrationPending,

        /** Transition status, right after [PasskeyRegistrationPending], before the 1st a passkey-bound token is obtained. */
        FirstPasskeyAuthenticationPending,

        /** Account successfully connected, with registered passkey. */
        LoggedIn,

        RestoringFromBackup,
    }
}
