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

import com.infomaniak.auth.lib.models.migration.OrganizationAccount
import com.infomaniak.auth.lib.models.migration.user.Email
import com.infomaniak.auth.lib.models.migration.user.Phone
import com.infomaniak.auth.lib.models.migration.user.UserProfile
import com.infomaniak.auth.lib.models.migration.user.preferences.Country
import com.infomaniak.auth.lib.models.migration.user.preferences.Language
import com.infomaniak.auth.lib.models.migration.user.preferences.OrganizationPreference
import com.infomaniak.auth.lib.models.migration.user.preferences.Preferences
import com.infomaniak.auth.lib.models.migration.user.preferences.TimeZone
import com.infomaniak.auth.lib.models.migration.user.preferences.security.AuthDevices
import com.infomaniak.auth.lib.models.migration.user.preferences.security.Security
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.lib.login.ApiToken
import com.infomaniak.auth.lib.models.migration.ApiToken as MigrationApiToken

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
        phones = phones?.mapTo(ArrayList()) { it.toCorePhone() },
        emails = emails?.mapTo(ArrayList()) { it.toCoreEmail() },
        apiToken = apiToken.toCoreApiToken(),
        organizations = organizations.mapTo(ArrayList()) { it.toCoreOrganizationAccount() },
    )
}

private fun Preferences.toCorePreferences() =
    com.infomaniak.core.auth.models.user.preferences.Preferences(
        security = security?.toCoreSecurity(),
        organizationPreference = organizationPreference.toCoreOrganizationPreference(),
        language = language.toCoreLanguage(),
        country = country.toCoreCountry(),
        timezone = timezone?.toCoreTimeZone(),
    )

private fun Language.toCoreLanguage() =
    com.infomaniak.core.auth.models.user.preferences.Language(
        shortName = shortName,
        locale = locale,
        shortLocale = shortLocale,
    )

private fun Country.toCoreCountry() =
    com.infomaniak.core.auth.models.user.preferences.Country(
        shortName = shortName,
        isEnabled = isEnabled,
    )

private fun TimeZone.toCoreTimeZone() =
    com.infomaniak.core.auth.models.user.preferences.TimeZone(
        gmt = gmt,
    )

private fun OrganizationPreference.toCoreOrganizationPreference() =
    com.infomaniak.core.auth.models.user.preferences.OrganizationPreference(
        currentOrganizationId = currentOrganizationId,
        lastLoginAt = lastLoginAt,
    )

private fun Security.toCoreSecurity() =
    com.infomaniak.core.auth.models.user.preferences.security.Security(
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

private fun AuthDevices.toCoreAuthDevices() =
    com.infomaniak.core.auth.models.user.preferences.security.AuthDevices(
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

private fun Phone.toCorePhone() = com.infomaniak.core.auth.models.user.Phone(phone = phone)

private fun Email.toCoreEmail() = com.infomaniak.core.auth.models.user.Email(email = email)

private fun MigrationApiToken.toCoreApiToken() = ApiToken(
    accessToken = accessToken,
    tokenType = tokenType,
    userId = userId,
)

private fun OrganizationAccount.toCoreOrganizationAccount() = com.infomaniak.core.auth.models.OrganizationAccount(
    id = id,
    name = name,
    type = com.infomaniak.core.auth.models.OrganizationAccount.Type.valueOf(type.name),
    billing = billing,
    mailing = mailing,
    noAccess = noAccess,
    workspaceOnly = workspaceOnly,
    billingMailing = billingMailing,
    legalEntityType = legalEntityType,
)
