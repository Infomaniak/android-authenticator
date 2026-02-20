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
package com.infomaniak.auth.lib

import com.infomaniak.auth.lib.PublicKeyUtils.getPublicKeyCose
import com.infomaniak.auth.lib.network.models.ClientExtensionResults
import com.infomaniak.auth.lib.network.models.PasskeysOptions
import com.infomaniak.auth.lib.network.models.RegisterPasskey
import com.infomaniak.auth.lib.network.models.RegisterPasskeyResponse
import com.infomaniak.auth.lib.network.models.WebAuthnAttestationObject
import com.infomaniak.auth.lib.network.models.WebAuthnClientData
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.encodeUtf8
import kotlin.io.encoding.Base64
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RegisterPasskeyBuilder(
    private val passkeysOptions: PasskeysOptions,
    private val publicKey: ByteArray,
) {

    fun build(): RegisterPasskey {
        val publicKeyCose = getPublicKeyCose(publicKey)
        val randomInt = Random(passkeysOptions.challenge.hashCode().toLong())
        val stringId = "$randomInt${passkeysOptions.user}${passkeysOptions.relyingParty}"
        val rawId = stringId.encodeToByteArray()
        val id = Base64.encode(rawId)

        // AttestationObject
        val attestationObject = generateAttestationObject(
            rpId = passkeysOptions.relyingParty.id,
            credentialId = rawId,
            publicKeyCose = publicKeyCose,
        )

        // AuthenticatorData
        // Using rawId for credentialId because it has to be something unique
        val authenticatorData = generateAuthData(
            rpId = passkeysOptions.relyingParty.id,
            credentialId = rawId,
            publicKeyCose = publicKeyCose
        )
        val clientData = WebAuthnClientData(
            type = "webauthn.create",
            challenge = passkeysOptions.challenge,
            origin = "infomaniak.com",
            crossOrigin = false,
        )

        val response = RegisterPasskeyResponse(
            attestationObject = Base64.encode(Cbor.encodeToByteArray(attestationObject)),
            clientDataJSON = Base64.encode(Json.encodeToString(clientData).encodeToByteArray()),
            transports = listOf("internal"),
            publicKeyAlgorithm = -7,
            publicKey = Base64.UrlSafe.encode(publicKey),
            authenticatorData = Base64.encode(authenticatorData),
        )
        val type = "public-key"
        val clientExtensionResult = ClientExtensionResults
        val authenticatorAttachment = "platform"

        return RegisterPasskey(
            device = Uuid.random().toHexDashString(),
            id = id,
            rawId = Base64.encode(rawId),
            registerPasskeyResponse = response,
            type = type,
            clientExtensionResults = clientExtensionResult,
            authenticatorAttachment = authenticatorAttachment,
        )
    }

    private fun generateAuthData(rpId: String, credentialId: ByteArray, publicKeyCose: ByteArray): ByteArray {
        val rpIdHash = rpId.encodeUtf8().sha256().toByteArray()
        val flags: Byte = 0x41
        val signCount = byteArrayOf(0x00, 0x00, 0x00, 0x00)

        // attestedCredentialData = AAGUID (16 bytes) + credentialId length + credentialId + publicKeyCose
        val aaguid = ByteArray(16) { 0x00 }
        val credentialIdLength = byteArrayOf((credentialId.size shr 8).toByte(), (credentialId.size and 0xFF).toByte())

        return rpIdHash + flags + signCount + aaguid + credentialIdLength + credentialId + publicKeyCose
    }

    private fun generateAttestationObject(rpId: String, credentialId: ByteArray, publicKeyCose: ByteArray): ByteArray {
        val authData = generateAuthData(rpId, credentialId, publicKeyCose)

        val attestationObject = WebAuthnAttestationObject(
            fmt = "none",
            authData = authData
        )

        return Cbor.encodeToByteArray(attestationObject)
    }
}
