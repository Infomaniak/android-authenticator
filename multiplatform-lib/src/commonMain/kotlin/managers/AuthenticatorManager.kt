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
package com.infomaniak.auth.lib.managers

import com.infomaniak.auth.lib.CryptoObjectsBuilder
import com.infomaniak.auth.lib.internal.KeyPairManagerImpl
import com.infomaniak.auth.lib.network.models.ClientExtensionResults
import com.infomaniak.auth.lib.network.models.VerifyAuthenticationData
import com.infomaniak.auth.lib.network.models.VerifyResponse
import com.infomaniak.auth.lib.network.repositories.WebAuthnRepository
import com.infomaniak.auth.lib.utils.SignUtils.signWithPrivateKey
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.toByteString

class AuthenticatorManager(private val webAuthnRepository: WebAuthnRepository) {

    private val cryptoObjectsBuilder by lazy { CryptoObjectsBuilder() }
    private val keyPairManager by lazy { KeyPairManagerImpl() }

    private val base64NoPadding get() = cryptoObjectsBuilder.base64NoPadding

    suspend fun registerPasskey(token: String, userId: Int) {
        val passkeysOptions = webAuthnRepository.getPasskeysOptions(token).data
        val keyIds = cryptoObjectsBuilder.getKeyIds()
        val keyIdAsByteArray = keyIds.first
        val keyIdAsString = keyIds.second
        keyPairManager.generateNewKey(userId, keyIdAsString)?.let {
            throw Exception("Couldn't generate new key: ${it.details}")
        }
        val publicKeyAsByteArray = keyPairManager.retrievePublicKey(userId, keyIdAsString).firstOrNull()!!

        val registerPasskey = cryptoObjectsBuilder.buildRegisterPasskey(
            publicKey = publicKeyAsByteArray,
            passkeysOptions = passkeysOptions,
            rawId = keyIdAsByteArray,
            id = keyIdAsString,
        )

        webAuthnRepository.registerPasskey(token, registerPasskey)
    }

    suspend fun getToken(clientId: String, userId: Int, keyId: String): String {
        val authenticationOptions = webAuthnRepository.challenge(clientId)
        val rawAuthenticatorData = cryptoObjectsBuilder.generateAuthenticatorData(
            publicKey = keyPairManager.retrievePublicKey(userId, keyId).firstOrNull()!!,
            rpId = "infomaniak.ch",
            credentialId = keyId.toByteArray(),
        )
        val authenticatorData = base64NoPadding.encode(rawAuthenticatorData)

        val clientData = cryptoObjectsBuilder.buildClientData(authenticationOptions.challenge)
        val clientDataJsonBytes = Json.encodeToString(clientData).encodeToByteArray()
        val clientDataJsonHash = clientDataJsonBytes.toByteString().sha256().toByteArray()

        val verifyAuthenticationData = VerifyAuthenticationData(
            clientId = clientId,
            session = authenticationOptions.session,
            id = keyId,
            rawId = keyId,
            response = VerifyResponse(
                authenticatorData = authenticatorData,
                clientDataJSON = base64NoPadding.encode(clientDataJsonBytes),
                signature = base64NoPadding.encode(
                    signWithPrivateKey(
                        privateKey = keyPairManager.retrievePrivateKey(userId, keyId).firstOrNull()!!,
                        data = rawAuthenticatorData + clientDataJsonHash,
                    )
                ),
                userHandle = base64NoPadding.encode(userId.toString().toByteArray())
            ),
            type = "public-key",
            clientExtensionResults = ClientExtensionResults,
            authenticatorAttachment = "platform",
        )
        return webAuthnRepository.verify(verifyAuthenticationData).accessToken
    }
}
