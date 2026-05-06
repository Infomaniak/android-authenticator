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
import com.infomaniak.auth.lib.models.migration.user.preferences.Preferences
import com.infomaniak.auth.lib.models.migration.user.preferences.SharedCountry
import com.infomaniak.auth.lib.models.migration.user.preferences.SharedLanguage
import com.infomaniak.auth.lib.models.migration.user.preferences.SharedOrganizationPreference
import com.infomaniak.auth.lib.models.migration.user.preferences.SharedTimeZone
import com.infomaniak.auth.lib.models.migration.user.preferences.security.SharedAuthDevices
import com.infomaniak.auth.lib.models.migration.user.preferences.security.SharedSecurity
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.auth.lib.models.migration.SharedApiToken
import com.infomaniak.core.auth.models.user.preferences.Country
import com.infomaniak.core.auth.models.user.preferences.Language
import com.infomaniak.core.auth.models.user.preferences.OrganizationPreference
import com.infomaniak.core.auth.models.user.preferences.Preferences as CorePreferences
import com.infomaniak.core.auth.models.user.preferences.TimeZone
import com.infomaniak.core.auth.models.user.preferences.security.AuthDevices
import com.infomaniak.core.auth.models.user.preferences.security.Security
import com.infomaniak.lib.login.ApiToken as LoginApiToken

fun LoginApiToken.toSharedApiToken(): SharedApiToken {
    return SharedApiToken(
        accessToken = accessToken,
        tokenType = tokenType,
        userId = userId,
    )
}

fun SharedApiToken.toLoginApiToken() = LoginApiToken(
    accessToken = accessToken,
    tokenType = tokenType,
    userId = userId,
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
    language = language.toLanguage(),
    country = country.toCountry(),
    timezone = timezone?.toTimeZone(),
)

private fun CorePreferences.toPreferences() = Preferences(
    security = security?.toSharedSecurity(),
    organizationPreference = organizationPreference.toSharedOrganizationPreference(),
    language = language.toSharedLanguage(),
    country = country.toSharedCountry(),
    timezone = timezone?.toSharedTimeZone(),
)

private fun SharedLanguage.toLanguage() = Language(
    shortName = shortName,
    locale = locale,
    shortLocale = shortLocale,
)

private fun Language.toSharedLanguage() = SharedLanguage(
    shortName = shortName,
    locale = locale,
    shortLocale = shortLocale,
)

private fun SharedCountry.toCountry() = Country(
    shortName = shortName,
    isEnabled = isEnabled,
)

private fun Country.toSharedCountry() = SharedCountry(
    shortName = shortName,
    isEnabled = isEnabled,
)

private fun SharedTimeZone.toTimeZone() = TimeZone(
    gmt = gmt,
)

private fun TimeZone.toSharedTimeZone() = SharedTimeZone(
    gmt = gmt,
)

private fun SharedOrganizationPreference.toOrganizationPreference() = OrganizationPreference(
    currentOrganizationId = currentOrganizationId,
    lastLoginAt = lastLoginAt,
)

private fun OrganizationPreference.toSharedOrganizationPreference() = SharedOrganizationPreference(
    currentOrganizationId = currentOrganizationId,
    lastLoginAt = lastLoginAt,
)

private fun SharedSecurity.toSecurity() = Security(
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
    authDevices = authDevices?.mapTo(ArrayList()) { it.toAuthDevices() },
)

private fun Security.toSharedSecurity() = SharedSecurity(
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
    authDevices = authDevices?.mapTo(ArrayList()) { it.toSharedAuthDevices() },
)

private fun SharedAuthDevices.toAuthDevices() = AuthDevices(
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

private fun AuthDevices.toSharedAuthDevices() = SharedAuthDevices(
    id = id,
    name = name,
    lastConnexion = lastConnexion,
    userAgent = userAgent,
    userIp = userIp,
    device = device,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)
