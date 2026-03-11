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

import com.infomaniak.auth.lib.internal.utils.getKeyCoordinates
import com.infomaniak.auth.lib.internal.webauthn.KeyAlgorithm
import com.infomaniak.auth.lib.internal.webauthn.createEncodedWebAuthnAttestationObject
import com.infomaniak.auth.lib.internal.webauthn.keyCoseOf
import com.infomaniak.auth.lib.network.models.ClientExtensionResults
import com.infomaniak.auth.lib.network.models.PasskeysOptions
import com.infomaniak.auth.lib.network.models.RegisterPasskey
import com.infomaniak.auth.lib.network.models.RegisterPasskeyResponse
import com.infomaniak.auth.lib.network.models.WebAuthnClientData
import com.infomaniak.auth.lib.utils.getDeviceInfo
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.encodeUtf8
import kotlin.io.encoding.Base64
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// This class should be internal
@OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)
class CryptoObjectsBuilder() {

    private val base64NoPadding = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

    fun getKeyIds(): Pair<ByteArray, String> {
        val rawId = Random.nextBytes(16)
        val id = base64NoPadding.encode(rawId)

        return rawId to id
    }

    fun buildRegisterPasskey(
        publicKey: ByteArray,
        passkeysOptions: PasskeysOptions,
        rawId: ByteArray,
        id: String,
    ): RegisterPasskey {
        val authenticatorData = generateAuthenticatorData(
            publicKey = publicKey,
            rpId = passkeysOptions.relyingParty.id,
            credentialId = rawId,
        )
        val clientDataJSON = buildClientDataJSON(passkeysOptions.challenge)

        // AttestationObject
        val attestationObject = createEncodedWebAuthnAttestationObject(
            fmt = "none",
            authData = authenticatorData
        )
        val response = RegisterPasskeyResponse(
            attestationObject = base64NoPadding.encode(attestationObject),
            clientDataJSON = clientDataJSON,
            transports = listOf("internal"),
            publicKey = base64NoPadding.encode(publicKey),
            authenticatorData = base64NoPadding.encode(authenticatorData),
            publicKeyAlgorithm = KeyAlgorithm.ES256,
        )
        val type = "public-key"
        val clientExtensionResult = ClientExtensionResults
        val authenticatorAttachment = "platform"

        return RegisterPasskey(
            session = passkeysOptions.session,
            device = getDeviceInfo(),
            id = id,
            rawId = base64NoPadding.encode(rawId),
            registerPasskeyResponse = response,
            type = type,
            clientExtensionResults = clientExtensionResult,
            authenticatorAttachment = authenticatorAttachment,
        )
    }

    fun buildClientDataJSON(challenge: String): String {
        val clientData = WebAuthnClientData(
            type = "webauthn.create",
            challenge = challenge,
            origin = "https://infomaniak.ch",
            crossOrigin = false,
        )

        return base64NoPadding.encode(Json.encodeToString(clientData).encodeToByteArray())
    }

    fun generateAuthenticatorData(publicKey: ByteArray, rpId: String, credentialId: ByteArray): ByteArray {
        val keyCoordinates = getKeyCoordinates(publicKey)
        val publicKeyCose = keyCoseOf(
            x = keyCoordinates.x,
            y = keyCoordinates.y
        )
        val rpIdHash = rpId.encodeUtf8().sha256().toByteArray()
        val flags: Byte = 0x41
        val signCount = byteArrayOf(0x00, 0x00, 0x00, 0x00)

        val aaguid = Uuid.random().toByteArray() //TODO Might need to do this only once to have something unique for the App
        val credentialIdLength = byteArrayOf((credentialId.size shr 8).toByte(), (credentialId.size and 0xFF).toByte())

        val buffer = Buffer()
        buffer.write(rpIdHash)
        buffer.writeByte(flags)
        buffer.write(signCount)
        buffer.write(aaguid)
        buffer.write(credentialIdLength)
        buffer.write(credentialId)
        buffer.write(publicKeyCose)
        return buffer.readByteArray()
    }
}
