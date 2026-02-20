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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64

@Serializable
data class CoseKey(
    val kty: Int,
    val alg: Int,
    val crv: Int,
    @Serializable(with = ByteArrayAsByteListSerializer::class)
    val x: ByteArray,
    @Serializable(with = ByteArrayAsByteListSerializer::class)
    val y: ByteArray,
)

object ByteArrayAsByteListSerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ByteArrayAsByteList", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray) {
        // On encode le ByteArray comme une string Base64 URL-safe
        val base64 = Base64.UrlSafe.encode(value)
        encoder.encodeString(base64)
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        val base64 = decoder.decodeString()
        return Base64.UrlSafe.decode(base64)
    }
}
