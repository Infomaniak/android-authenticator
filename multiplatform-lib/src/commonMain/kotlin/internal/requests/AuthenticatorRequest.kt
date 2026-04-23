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
package com.infomaniak.auth.lib.internal.requests

import com.infomaniak.auth.lib.internal.models.AuthResult
import com.infomaniak.auth.lib.internal.models.AuthenticationOptions
import com.infomaniak.auth.lib.internal.models.MigrationOptions
import com.infomaniak.auth.lib.internal.models.OtpPayload
import com.infomaniak.auth.lib.internal.models.PasskeysOptions
import com.infomaniak.auth.lib.internal.models.RegisterPasskey
import com.infomaniak.auth.lib.internal.models.SuccessfulApiResponse
import com.infomaniak.auth.lib.internal.models.VerifyAuthenticationData
import com.infomaniak.auth.lib.internal.network.ApiRoutes
import com.infomaniak.auth.lib.internal.network.utils.decode
import com.infomaniak.auth.lib.models.migration.user.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class AuthenticatorRequest(private val httpClient: HttpClient) {

    /**
     * Retrieves options (including a challenge) prior to registering a public key credential with [registerPasskey].
     */
    suspend fun getPasskeysOptions(token: String): SuccessfulApiResponse<PasskeysOptions> {
        return httpClient.get(ApiRoutes.passkeysOptions()) {
            addAuthenticationHeader(token)
        }.decode()
    }

    /**
     * Registers a public key credential (from the on-device generated private/public key pair),
     * after [getPasskeysOptions] is done.
     */
    suspend fun registerPasskey(token: String, registerPasskey: RegisterPasskey) {
        httpClient.post(ApiRoutes.registerPasskey()) {
            addAuthenticationHeader(token)
            setBody(registerPasskey)
        }
    }

    /**
     * Retrieves the backend-generated challenge, prior to authenticating with [verify].
     */
    suspend fun challenge(clientId: String): SuccessfulApiResponse<AuthenticationOptions> {
        return httpClient.post(ApiRoutes.challenge()) {
            setBody(mapOf("client_id" to clientId))
        }.decode()
    }

    /**
     * Authenticates with [VerifyAuthenticationData], which contains private-key signed data.
     *
     * That data includes the challenge retrieved in [challenge].
     *
     * @return An [AuthResult] that includes an access token.
     */
    suspend fun verify(verifyAuthenticationData: VerifyAuthenticationData): SuccessfulApiResponse<AuthResult> {
        return httpClient.post(ApiRoutes.verify()) {
            setBody(verifyAuthenticationData)
        }.decode()
    }

    /**
     * Delete an existing passkey.
     *
     * @param token The access token of the user.
     * @param passkeyId The id of the passkey to delete.
     */
    suspend fun deletePasskey(token: String, passkeyId: String) {
        httpClient.delete(ApiRoutes.delete(passkeyId)) {
            addAuthenticationHeader(token)
        }
    }

    /**
     * Get migration options (see [MigrationOptions]), prior to migration from kAuth.
     *
     * @param deviceId The id of the device.
     * @param userId The id of the user.
     */
    suspend fun getMigrationOptions(deviceId: String, userId: Long): SuccessfulApiResponse<MigrationOptions> {
        return httpClient.post(ApiRoutes.migrationsOptions()) {
            setBody(mapOf("device" to deviceId, "id" to userId.toString()))
        }.decode()
    }

    /**
     * Starts the passkey migration process from kAuth.
     *
     * Requires a call to [getMigrationOptions] to get the time data that will be used to generate the [otp],
     * and the [sessionId].
     *
     * The returned [AuthResult] contains an access token, to be used to register a passkey with [registerPasskey].
     *
     * @param sessionId ID of the session you get from [getMigrationOptions].
     * @param otpPayload Object representing the data we need to send to the server to get an access token
     */
    suspend fun getTokenForMigration(
        sessionId: String,
        otpPayload: OtpPayload,
    ): SuccessfulApiResponse<AuthResult> {
        return httpClient.post(ApiRoutes.verifyMigration(sessionId)) {
            setBody(otpPayload)
        }.decode()
    }

    /**
     * Completes the passkey migration process from kAuth, after [getTokenForMigration] has succeeded.
     *
     * @param token The access token of the user.
     * @param deviceId ID of the device.
     */
    suspend fun completeMigration(token: String, sessionId: String, deviceId: String) {
        httpClient.delete(ApiRoutes.finishMigration(sessionId)) {
            addAuthenticationHeader(token)
            setBody(mapOf("device" to deviceId))
        }
    }

    suspend fun getUserProfile(
        token: String,
    ): SuccessfulApiResponse<UserProfile> {
        val url = "${ApiRoutes.userProfile()}&with=security"

        return httpClient.get(url) {
            addAuthenticationHeader(token)
        }.decode()
    }

    private fun HttpRequestBuilder.addAuthenticationHeader(token: String) {
        headers {
            append("Authorization", "Bearer $token")
        }
    }
}
