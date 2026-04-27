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
package com.infomaniak.auth.lib.internal.managers

import com.infomaniak.auth.lib.internal.CryptoObjectsBuilder
import com.infomaniak.auth.lib.internal.Failure
import com.infomaniak.auth.lib.internal.KeyPairManagerImpl
import com.infomaniak.auth.lib.internal.extensions.firstOrElse
import com.infomaniak.auth.lib.internal.models.ClientExtensionResults
import com.infomaniak.auth.lib.internal.models.VerifyAuthenticationData
import com.infomaniak.auth.lib.internal.models.VerifyResponse
import com.infomaniak.auth.lib.internal.repositories.AccountsRepository
import com.infomaniak.auth.lib.internal.repositories.WebAuthnRepository
import com.infomaniak.auth.lib.internal.utils.SignUtils
import com.infomaniak.auth.lib.internal.utils.Xor
import com.infomaniak.auth.lib.models.migration.ApiToken
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.toByteString

internal class AuthenticatorManager(
    private val webAuthnRepository: WebAuthnRepository,
    private val accountsRepository: AccountsRepository,
) {

    private val cryptoObjectsBuilder by lazy { CryptoObjectsBuilder() }
    private val keyPairManager by lazy { KeyPairManagerImpl() }

    private val base64NoPadding get() = cryptoObjectsBuilder.base64UrlSafeNoPadding

    suspend fun getUserProfile(token: String) = webAuthnRepository.getUserProfile(token)

    suspend fun registerPasskey(token: String, userId: Long): String {
        val passkeysOptions = webAuthnRepository.getPasskeysOptions(token).data
        val keyIds = cryptoObjectsBuilder.getKeyIds()
        val keyIdAsByteArray = keyIds.first
        val keyIdAsString = keyIds.second
        keyPairManager.generateNewKey(userId, keyIdAsString)?.let { failure ->
            error("Couldn't generate new key: ${failure.details}")
        }
        val publicKeyAsByteArray = keyPairManager.retrievePublicKey(userId, keyIdAsString).firstOrElse {
            error("Couldn't retrieve public key (registerPasskey): ${it.details}")
        }

        val registerPasskey = cryptoObjectsBuilder.buildRegisterPasskey(
            publicKey = publicKeyAsByteArray,
            passkeysOptions = passkeysOptions,
            rawId = keyIdAsByteArray,
            id = keyIdAsString,
        )

        webAuthnRepository.registerPasskey(token, registerPasskey)

        return keyIdAsString
    }

    suspend fun getToken(
        clientId: String,
        userId: Long,
        keyIdFromOldPasskey: String? = null,
    ): Xor<ApiToken, Failure.KeyManagement.KeyNotFound> {
        val keyId = keyIdFromOldPasskey ?: keyPairManager.findKeyIdFor { it.startsWith("$userId-") }.firstOrNull()
        ?: return Xor.Second(Failure.KeyManagement.KeyNotFound("No key found for user $userId"))

        val authenticationOptions = webAuthnRepository.challenge(clientId)
        val publicKey = keyPairManager.retrievePublicKey(userId, keyId).firstOrNull()
            ?: return Xor.Second(Failure.KeyManagement.KeyNotFound("No public key found for $userId"))
        val rawAuthenticatorData = cryptoObjectsBuilder.generateAuthenticatorData(
            publicKey = publicKey,
            rpId = "infomaniak.ch",
            credentialId = keyId.toByteArray(),
        )
        val authenticatorData = base64NoPadding.encode(rawAuthenticatorData)

        val clientData = cryptoObjectsBuilder.buildClientData(authenticationOptions.challenge)
        val clientDataJsonBytes = Json.encodeToString(clientData).encodeToByteArray()
        val clientDataJsonHash = clientDataJsonBytes.toByteString().sha256().toByteArray()

        val privateKey = keyPairManager.retrievePrivateKey(userId, keyId).firstOrNull()
            ?: return Xor.Second(Failure.KeyManagement.KeyNotFound("No private key found for $userId"))
        val verifyAuthenticationData = VerifyAuthenticationData(
            clientId = clientId,
            session = authenticationOptions.session,
            id = keyId,
            rawId = keyId,
            response = VerifyResponse(
                authenticatorData = authenticatorData,
                clientDataJSON = base64NoPadding.encode(clientDataJsonBytes),
                signature = base64NoPadding.encode(
                    SignUtils.signWithPrivateKey(
                        privateKey = privateKey,
                        data = rawAuthenticatorData + clientDataJsonHash,
                    )
                ),
                userHandle = base64NoPadding.encode(userId.toString().toByteArray())
            ),
            type = "public-key",
            clientExtensionResults = ClientExtensionResults,
            authenticatorAttachment = "platform",
        )
        val verifyAuthData = webAuthnRepository.verify(verifyAuthenticationData)
        val apiToken = ApiToken(
            accessToken = verifyAuthData.accessToken,
            tokenType = verifyAuthData.tokenType,
            userId = userId.toInt(),
            scope = verifyAuthData.scope,
        )
        return Xor.First(apiToken)
    }

    suspend fun removeAccount(token: String, userId: Long) {
        val passkeyId = keyPairManager.findKeyIdFor { it.startsWith("$userId-") }.firstOrNull()

        if (passkeyId != null) {
            // If we have a passkey for this account, revoke it against the backend and delete it
            webAuthnRepository.deletePasskey(token, passkeyId)
            val _ = keyPairManager.deleteKeysMatching { "-$passkeyId-" in it }
        }

        accountsRepository.deleteAccount(userId)
    }

    suspend fun deleteKeysFor(userId: Long) {
        val _ = keyPairManager.deleteKeysMatching { it.startsWith("$userId-") }
    }

    suspend fun getKeyIdFor(userId: Long): String? {
        return keyPairManager.findKeyIdFor({ it.startsWith("$userId-") }).firstOrNull()
    }
}
