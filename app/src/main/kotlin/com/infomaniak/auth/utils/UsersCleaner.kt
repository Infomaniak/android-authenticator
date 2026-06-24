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
package com.infomaniak.auth.utils

import com.infomaniak.auth.lib.AuthenticatorFacade
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UsersCleaner @Inject constructor(
    private val accountUtils: AccountUtils,
    private val authenticatorFacade: AuthenticatorFacade,
) {

    suspend fun cleanOrphanUsers() {
        val users = accountUtils.users.first()
        val accountsIds = authenticatorFacade.accounts.first().map { it.id }

        users.forEach { user ->
            if (user.id.toLong() !in accountsIds) accountUtils.removeUser(user.id)
        }
    }
}
