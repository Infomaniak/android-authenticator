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
package network.repositories

import com.infomaniak.auth.lib.network.ApiClientProvider
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import network.models.Answer
import network.models.AuthResult
import network.models.AuthenticationOptions
import network.models.RegisterCredential
import network.models.RegistrationOptions
import network.models.VerifyAuthenticationData
import network.requests.AuthenticatorRequest
import network.utils.ApiEnvironment

class WebAuthnRepository internal constructor(private val authenticatorRequest: AuthenticatorRequest) {

    constructor(environment: ApiEnvironment) : this(ApiClientProvider(), environment)
    constructor(apiClientProvider: ApiClientProvider = ApiClientProvider(), environment: ApiEnvironment) : this(
        environment = environment,
        json = apiClientProvider.json,
        httpClient = apiClientProvider.httpClient,
    )

    internal constructor(environment: ApiEnvironment, json: Json, httpClient: HttpClient) :
            this(AuthenticatorRequest(environment, json, httpClient))

    // Authentification challenge (not authentified)
    suspend fun getAuthenticationOptions(identity: Long): AuthenticationOptions {
        return authenticatorRequest.getAuthenticationOptions(identity)
    }

    // Authentification verification (not authentified)
    suspend fun verifyAuthentication(identity: Long, answer: Answer): AuthResult {
        return authenticatorRequest.verifyAuthentication(VerifyAuthenticationData(identity, answer))
    }

    // Generate WebAuthn registration options (authentified)
    suspend fun getRegistrationOptions(): RegistrationOptions {
        //TODO where to get the bearer ?
        return authenticatorRequest.getRegistrationOptions()
    }

    // Validate WebAuthn registration and save public key (authentified)
    suspend fun registerCredential(registerCredential: RegisterCredential): Result<Unit> {
        //TODO where to get the bearer ?
        return authenticatorRequest.registerCredential(registerCredential)
    }
}
