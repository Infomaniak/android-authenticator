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

import com.infomaniak.auth.lib.models.migration.user.UserProfile
import com.infomaniak.auth.lib.models.migration.user.preferences.Country
import com.infomaniak.auth.lib.models.migration.user.preferences.Language
import com.infomaniak.auth.lib.models.migration.user.preferences.OrganizationPreference
import com.infomaniak.auth.lib.models.migration.user.preferences.Preferences
import com.infomaniak.auth.lib.models.migration.user.preferences.TimeZone
import com.infomaniak.auth.lib.models.migration.user.preferences.security.AuthDevices
import com.infomaniak.auth.lib.models.migration.user.preferences.security.Security
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.auth.lib.models.migration.ApiToken as MigrationApiToken
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

fun UserProfile.toUser(): User {
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

private fun Language.toCoreLanguage() = CoreLanguage(
    shortName = shortName,
    locale = locale,
    shortLocale = shortLocale,
)

private fun Country.toCoreCountry() = CoreCountry(
    shortName = shortName,
    isEnabled = isEnabled,
)

private fun TimeZone.toCoreTimeZone() = CoreTimeZone(
    gmt = gmt,
)

private fun OrganizationPreference.toCoreOrganizationPreference() = CoreOrganizationPreference(
    currentOrganizationId = currentOrganizationId,
    lastLoginAt = lastLoginAt,
)

private fun Security.toCoreSecurity() = CoreSecurity(
    score = score,
    hasRecoveryEmail = hasRecoveryEmail,
    hasValidPhone = hasValidPhone,
    emailValidatedAt = emailValidatedAt,
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

private fun AuthDevices.toCoreAuthDevices() = CoreAuthDevices(
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
