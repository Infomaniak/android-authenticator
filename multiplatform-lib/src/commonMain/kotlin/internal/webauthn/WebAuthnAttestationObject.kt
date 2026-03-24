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
import kotlinx.serialization.encodeToByteArray

internal fun createEncodedWebAuthnAttestationObject(fmt: String, authData: ByteArray): ByteArray {
    val attestationObject = WebAuthnAttestationObject(
        fmt = fmt,
        authData = authData,
    )
    return Cbor.CoseCompliant.encodeToByteArray(attestationObject)
}

@Suppress("unused") // The properties are used by CBOR serialization, to be sent to the backend.
@Serializable
private class WebAuthnAttestationObject(
    val fmt: String,
    @ByteString
    val authData: ByteArray,
)
