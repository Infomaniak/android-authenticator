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
package com.infomaniak.auth.lib.network.interfaces

import com.infomaniak.auth.lib.models.migration.ApiToken
import com.infomaniak.auth.lib.models.migration.user.UserProfile

interface AuthenticatorBridge {
    suspend fun getTokenFromCrossAppLogin(userId: Long): ApiToken?
    suspend fun getTokenFromDatabase(userId: Long): String?
    suspend fun persistTokenForAccount(userId: Long, token: String)
    suspend fun persistUserProfile(userProfile: UserProfile)
}
