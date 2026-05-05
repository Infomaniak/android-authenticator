/*
 * Infomaniak Authenticator - Android
 * Copyright (C) 2022-2026 Infomaniak Network SA
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
package com.infomaniak.auth.lib.models.migration.user

import com.infomaniak.auth.lib.models.migration.SharedApiToken
import com.infomaniak.auth.lib.models.migration.user.preferences.Preferences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SharedUserProfile(
    val id: Int,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("first_name")
    val firstname: String,
    @SerialName("last_name")
    val lastname: String,
    val email: String,
    val avatar: String?,
    val login: String,
    @SerialName("is_staff")
    val isStaff: Boolean = false,
    val preferences: Preferences,

    /**
     * Local
     */
    @Transient
    var apiToken: SharedApiToken = SharedApiToken(accessToken = "", tokenType = "", userId = 0),
) {
    fun getInitials() = "${firstname.firstOrNull()?.uppercase() ?: ""}${lastname.firstOrNull()?.uppercase() ?: ""}"
}
