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
package com.infomaniak.auth.lib.models.migration.user.preferences.security

import androidx.room.ColumnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Security(
    var score: Int,
    @SerialName("has_recovery_email")
    var hasRecoveryEmail: Boolean,
    @SerialName("has_valid_phone")
    @ColumnInfo(defaultValue = "false")
    var hasValidPhone: Boolean,
    @SerialName("email_validated_at")
    var emailValidatedAt: Long,
    var otp: Boolean,
    var sms: Boolean,
    @SerialName("sms_phone")
    var smsPhone: String,
    var yubikey: Boolean,
    @SerialName("infomaniak_application")
    var infomaniakApplication: Boolean,
    @SerialName("double_auth")
    var doubleAuth: Boolean,
    @SerialName("remaining_rescue_code")
    var remainingRescueCode: Int,
    @SerialName("last_login_at")
    var lastLoginAt: Long,
    @SerialName("date_last_changed_password")
    var dateLastChangedPassword: Long,
    @SerialName("double_auth_method")
    var doubleAuthMethod: String,
    @SerialName("auth_devices")
    var authDevices: ArrayList<AuthDevices>?,
)
