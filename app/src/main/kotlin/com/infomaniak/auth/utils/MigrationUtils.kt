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

import com.infomaniak.multiplatform_authenticator.core.models.migration.SharedApiToken
import com.infomaniak.multiplatform_authenticator.core.models.migration.user.SharedUserProfile
import com.infomaniak.multiplatform_authenticator.core.models.migration.user.preferences.Preferences
import com.infomaniak.multiplatform_authenticator.core.models.migration.user.preferences.SharedOrganizationPreference
import com.infomaniak.multiplatform_authenticator.core.models.migration.user.preferences.security.SharedSecurity
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.auth.models.user.preferences.OrganizationPreference
import com.infomaniak.core.auth.models.user.preferences.security.Security
import com.infomaniak.core.auth.models.user.preferences.Preferences as CorePreferences
import com.infomaniak.core.login.ApiToken as LoginApiToken

fun LoginApiToken.toSharedApiToken(): SharedApiToken {
    return SharedApiToken(
        accessToken = accessToken,
        tokenType = tokenType,
        userId = userId,
        isTemporary = isTemporary,
        expiresAt = expiresAt,
        expiresIn = expiresIn,
        refreshToken = refreshToken,
        scope = scope,
    )
}

fun SharedApiToken.toLoginApiToken() = LoginApiToken(
    accessToken = accessToken,
    tokenType = tokenType,
    userId = userId,
    refreshToken = refreshToken,
    expiresIn = expiresIn,
    scope = scope,
    expiresAt = expiresAt,
    isTemporary = isTemporary,
)

fun SharedUserProfile.toUser(): User {
    return User(
        id = id,
        displayName = displayName,
        firstname = firstname,
        lastname = lastname,
        email = email,
        avatar = avatar,
        login = login,
        isStaff = isStaff,
        preferences = preferences.toCorePreferences(),
        apiToken = apiToken.toLoginApiToken(),
    )
}

fun User.toSharedUser(): SharedUserProfile {
    return SharedUserProfile(
        id = id,
        displayName = displayName,
        firstname = firstname,
        lastname = lastname,
        email = email,
        avatar = avatar,
        login = login,
        isStaff = isStaff,
        preferences = preferences.toPreferences(),
        apiToken = apiToken.toSharedApiToken(),
    )
}

private fun Preferences.toCorePreferences() = CorePreferences(
    security = security?.toSecurity(),
    organizationPreference = organizationPreference.toOrganizationPreference(),
)

private fun CorePreferences.toPreferences() = Preferences(
    security = security?.toSharedSecurity(),
    organizationPreference = organizationPreference.toSharedOrganizationPreference()
)

private fun SharedOrganizationPreference.toOrganizationPreference() = OrganizationPreference(
    currentOrganizationId = currentOrganizationId,
)

private fun OrganizationPreference.toSharedOrganizationPreference() = SharedOrganizationPreference(
    currentOrganizationId = currentOrganizationId,
)

private fun SharedSecurity.toSecurity() = Security(
    score = score,
    dateLastChangedPassword = dateLastChangedPassword,
)

private fun Security.toSharedSecurity() = SharedSecurity(
    score = score,
    dateLastChangedPassword = dateLastChangedPassword,
)
