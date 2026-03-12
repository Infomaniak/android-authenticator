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
@file:OptIn(ExperimentalSerializationApi::class)

package com.infomaniak.auth.lib.internal.webauthn

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.ByteString
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.cbor.CborLabel
import kotlinx.serialization.encodeToByteArray

/**
 * Returns the COSE of the key for our WebAuthn usage,
 * given its x and y coordinates (retrieved via OS specific APIs).
 *
 * **COSE** stands for **CBOR Object Signing and Encryption**.
 *
 * **CBOR** stands for **Concise Binary Object Representation**.
 */
internal fun keyCoseOf(x: ByteArray, y: ByteArray): ByteArray {
    require(x.size == 32) { "Invalid x coordinate length: ${x.size}" }
    require(y.size == 32) { "Invalid y coordinate length: ${y.size}" }
    val cose = KeyCose(
        kty = 2, // EC2
        alg = KeyAlgorithm.ES256,
        crv = 1, // P-256
        x = x,
        y = y,
    )
    return Cbor.CoseCompliant.encodeToByteArray(cose)
}

@Suppress("unused") // The properties are used by CBOR serialization, to be sent to the backend.
@Serializable
private class KeyCose(

    @CborLabel(1)
    val kty: Byte,

    @CborLabel(3)
    val alg: KeyAlgorithm,

    @CborLabel(-1)
    val crv: Byte,

    @ByteString
    @CborLabel(-2)
    val x: ByteArray,

    @ByteString
    @CborLabel(-3)
    val y: ByteArray,
)
