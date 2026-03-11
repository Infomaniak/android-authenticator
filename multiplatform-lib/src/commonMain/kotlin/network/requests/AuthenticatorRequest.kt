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
package network.requests

import com.infomaniak.auth.lib.network.models.AuthResult
import com.infomaniak.auth.lib.network.models.AuthenticationOptions
import com.infomaniak.auth.lib.network.models.PasskeysOptions
import com.infomaniak.auth.lib.network.models.RegisterPasskey
import com.infomaniak.auth.lib.network.models.SuccessfulApiResponse
import com.infomaniak.auth.lib.network.models.VerifyAuthenticationData
import io.ktor.client.HttpClient
import io.ktor.http.HeadersBuilder
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import network.utils.ApiEnvironment
import network.utils.ApiRoutes

internal class AuthenticatorRequest(
    environment: ApiEnvironment,
    json: Json,
    httpClient: HttpClient,
) : BaseRequest(environment, json, httpClient) {

    suspend fun getPasskeysOptions(token: String): SuccessfulApiResponse<PasskeysOptions> {
        return get(createUrl(ApiRoutes.getPasskeysOptions), appendHeaders = {
            addAuthenticationHeader(token)
        })
    }

    suspend fun registerPasskey(token: String, registerPasskey: RegisterPasskey) {
        post<Unit>(createUrl(ApiRoutes.registerPasskey), registerPasskey, appendHeaders = {
            addAuthenticationHeader(token)
        })
    }

    suspend fun challenge(clientId: String): SuccessfulApiResponse<AuthenticationOptions> {
        return post(createUrl(ApiRoutes.challenge), mapOf("client_id" to clientId))
    }

    suspend fun verify(verifyAuthenticationData: VerifyAuthenticationData): SuccessfulApiResponse<AuthResult> {
        return post(createUrl(ApiRoutes.verify), verifyAuthenticationData)
    }

    suspend fun deletePasskey(token: String, passkeyId: String) {
        return delete(Url("users/me/passkeys/$passkeyId"), appendHeaders = {
            addAuthenticationHeader(token)
        })
    }

    private fun HeadersBuilder.addAuthenticationHeader(token: String) {
        append("Authorization", "Bearer $token")
    }
}
