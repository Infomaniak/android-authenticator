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

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import network.repositories.AuthResult
import network.repositories.AuthenticationOptions
import network.repositories.RegisterCredential
import network.repositories.RegistrationOptions
import network.repositories.VerifyAuthenticationData
import network.utils.ApiEnvironment
import network.utils.ApiRoutes

internal class AuthenticatorRequest(
    environment: ApiEnvironment,
    json: Json,
    httpClient: HttpClient,
) : BaseRequest(environment, json, httpClient) {

    suspend fun getAuthenticationOptions(identity: Long): AuthenticationOptions {
        return post(createUrl(ApiRoutes.getAuthenticationOptions), mapOf("identity" to identity))
    }

    suspend fun verifyAuthentication(verifyAuthenticationData: VerifyAuthenticationData): AuthResult {
        return post(createUrl(ApiRoutes.verifyAuthentication), verifyAuthenticationData)
    }

    suspend fun getRegistrationOptions(): RegistrationOptions {
        return get(createUrl(ApiRoutes.getRegistrationOptions))
    }

    suspend fun registerCredential(registerCredential: RegisterCredential): Result<Unit> {
        return post(createUrl(ApiRoutes.registerCredential), registerCredential)
    }
}
