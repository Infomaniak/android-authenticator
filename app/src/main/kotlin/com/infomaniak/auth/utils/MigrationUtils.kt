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

import com.infomaniak.auth.lib.models.migration.user.SharedUserProfile
import com.infomaniak.auth.lib.models.migration.user.preferences.SharedCountry
import com.infomaniak.auth.lib.models.migration.user.preferences.SharedLanguage
import com.infomaniak.auth.lib.models.migration.user.preferences.SharedOrganizationPreference
import com.infomaniak.auth.lib.models.migration.user.preferences.Preferences
import com.infomaniak.auth.lib.models.migration.user.preferences.SharedTimeZone
import com.infomaniak.auth.lib.models.migration.user.preferences.security.SharedAuthDevices
import com.infomaniak.auth.lib.models.migration.user.preferences.security.SharedSecurity
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.auth.lib.models.migration.SharedApiToken as MigrationApiToken
import com.infomaniak.core.auth.models.user.preferences.Country as CoreCountry
import com.infomaniak.core.auth.models.user.preferences.Language as CoreLanguage
import com.infomaniak.core.auth.models.user.preferences.OrganizationPreference as CoreOrganizationPreference
import com.infomaniak.core.auth.models.user.preferences.Preferences as CorePreferences
import com.infomaniak.core.auth.models.user.preferences.TimeZone as CoreTimeZone
import com.infomaniak.core.auth.models.user.preferences.security.AuthDevices as CoreAuthDevices
import com.infomaniak.core.auth.models.user.preferences.security.Security as CoreSecurity
import com.infomaniak.lib.login.ApiToken as LoginApiToken

fun LoginApiToken.toMigrationApiToken(): MigrationApiToken {
    return MigrationApiToken(
        accessToken = accessToken,
        tokenType = tokenType,
        userId = userId,
    )
}

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

private fun Preferences.toCorePreferences() = CorePreferences(
    security = security?.toCoreSecurity(),
    organizationPreference = organizationPreference.toCoreOrganizationPreference(),
    language = language.toCoreLanguage(),
    country = country.toCoreCountry(),
    timezone = timezone?.toCoreTimeZone(),
)

private fun SharedLanguage.toCoreLanguage() = CoreLanguage(
    shortName = shortName,
    locale = locale,
    shortLocale = shortLocale,
)

private fun SharedCountry.toCoreCountry() = CoreCountry(
    shortName = shortName,
    isEnabled = isEnabled,
)

private fun SharedTimeZone.toCoreTimeZone() = CoreTimeZone(
    gmt = gmt,
)

private fun SharedOrganizationPreference.toCoreOrganizationPreference() = CoreOrganizationPreference(
    currentOrganizationId = currentOrganizationId,
    lastLoginAt = lastLoginAt,
)

private fun SharedSecurity.toCoreSecurity() = CoreSecurity(
    score = score,
    hasRecoveryEmail = hasRecoveryEmail,
    hasValidPhone = hasValidPhone,
    emailValidatedAt = emailValidatedAt ?: 0L,
    otp = otp,
    sms = sms,
    smsPhone = smsPhone,
    yubikey = yubikey,
    infomaniakApplication = infomaniakApplication,
    doubleAuth = doubleAuth,
    remainingRescueCode = remainingRescueCode,
    lastLoginAt = lastLoginAt,
    dateLastChangedPassword = dateLastChangedPassword,
    doubleAuthMethod = doubleAuthMethod,
    authDevices = authDevices?.mapTo(ArrayList()) { it.toCoreAuthDevices() },
)

private fun SharedAuthDevices.toCoreAuthDevices() = CoreAuthDevices(
    id = id,
    name = name,
    lastConnexion = lastConnexion ?: 0L,
    userAgent = userAgent,
    userIp = userIp,
    device = device,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)

private fun MigrationApiToken.toLoginApiToken() = LoginApiToken(
    accessToken = accessToken,
    tokenType = tokenType,
    userId = userId,
)
