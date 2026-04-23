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
package com.infomaniak.auth.lib.internal.repositories

import com.infomaniak.auth.lib.internal.models.AuthResult
import com.infomaniak.auth.lib.internal.models.AuthenticationOptions
import com.infomaniak.auth.lib.internal.models.MigrationOptions
import com.infomaniak.auth.lib.internal.models.OtpPayload
import com.infomaniak.auth.lib.internal.models.PasskeysOptions
import com.infomaniak.auth.lib.internal.models.RegisterPasskey
import com.infomaniak.auth.lib.internal.models.SuccessfulApiResponse
import com.infomaniak.auth.lib.internal.models.VerifyAuthenticationData
import com.infomaniak.auth.lib.internal.requests.AuthenticatorRequest

internal class WebAuthnRepository(
    private val authenticatorRequest: AuthenticatorRequest,
) {

    //region Passkey

    // Generate WebAuthn registration options (authentified)
    suspend fun getPasskeysOptions(token: String): SuccessfulApiResponse<PasskeysOptions> {
        return authenticatorRequest.getPasskeysOptions(token)
    }

    // Validate WebAuthn registration and save public key (authentified)
    suspend fun registerPasskey(token: String, registerPasskey: RegisterPasskey) {
        authenticatorRequest.registerPasskey(token, registerPasskey)
    }

    // Deletion of existing passkey (authentified)
    suspend fun deletePasskey(token: String, passkeyId: String) {
        authenticatorRequest.deletePasskey(token, passkeyId)
    }

    // Authentification challenge (not authentified)
    suspend fun challenge(clientId: String): AuthenticationOptions {
        return authenticatorRequest.challenge(clientId).data
    }

    // Authentification verification (not authentified)
    suspend fun verify(verifyAuthenticationData: VerifyAuthenticationData): AuthResult {
        return authenticatorRequest.verify(verifyAuthenticationData).data
    }

    //endregion

    //region Migration

    suspend fun getMigrationOptions(deviceId: String, userId: Long): MigrationOptions {
        return authenticatorRequest.getMigrationOptions(deviceId, userId).data
    }

    suspend fun getTokenForMigration(
        sessionId: String,
        otpPayload: OtpPayload,
    ): AuthResult {
        return authenticatorRequest.getTokenForMigration(sessionId, otpPayload).data
    }

    suspend fun completeMigration(token: String, sessionId: String, deviceId: String) {
        return authenticatorRequest.completeMigration(token, sessionId, deviceId)
    }

    suspend fun getUserProfile(token: String) = authenticatorRequest.getUserProfile(token = token).data

    //endregion
}
