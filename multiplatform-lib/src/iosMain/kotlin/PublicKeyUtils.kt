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

import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray

actual object PublicKeyUtils {

    actual fun getPublicKeyCose(publicKeyByteArray: ByteArray): ByteArray {
        require(publicKeyByteArray.size == 65) { "Invalid public key format" }
        require(publicKeyByteArray[0] == 0x04.toByte()) { "Expected uncompressed format" }

        val x = publicKeyByteArray.copyOfRange(1, 33)
        val y = publicKeyByteArray.copyOfRange(33, 65)

        val coseKey = CoseKey(
            kty = 2, // EC2
            alg = -7,// ES256
            crv = 1, // P-256
            x = x,   // x coord
            y = y    // y coord
        )
        return Cbor.encodeToByteArray(coseKey)
    }
}
