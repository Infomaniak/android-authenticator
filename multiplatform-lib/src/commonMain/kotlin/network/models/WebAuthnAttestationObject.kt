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
package com.infomaniak.auth.lib.network.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class WebAuthnAttestationObject(
    val fmt: String,
    @Serializable(with = ByteArrayAsByteListSerializer::class)
    val authData: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WebAuthnAttestationObject

        if (fmt != other.fmt) return false
        if (!authData.contentEquals(other.authData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fmt.hashCode()
        result = 31 * result + authData.contentHashCode()
        return result
    }
}

private object ByteArrayAsByteListSerializer : KSerializer<ByteArray> {
    private val delegate = ByteArraySerializer()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: ByteArray) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        return delegate.deserialize(decoder)
    }
}
