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

import okio.Buffer
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.spec.X509EncodedKeySpec

actual object PublicKeyUtils {

    actual fun getPublicKeyCose(publicKeyByteArray: ByteArray): ByteArray {
        val publicKey = getPublicKeyFromByteArray(publicKeyByteArray) as ECPublicKey
        val w = publicKey.w
        val x = w.affineX.toByteArray().padTo32Bytes()
        val y = w.affineY.toByteArray().padTo32Bytes()
        val buffer = Buffer()

        // Map with 5 elements
        buffer.writeByte(0xA5)

        // 1 (kty) -> 2 (EC2)
        buffer.writeByte(0x01)  // unsigned int 1
        buffer.writeByte(0x02)  // unsigned int 2

        // 3 (alg) -> -7 (ES256)
        buffer.writeByte(0x03)  // unsigned int 3
        buffer.writeByte(0x26)  // negative int -7 (0x26 = -1 - 6)

        // -1 (crv) -> 1 (P-256)
        buffer.writeByte(0x20)  // negative int -1 (0x20 = -1 - 0)
        buffer.writeByte(0x01)  // unsigned int 1

        // -2 (x) -> ByteString(32)
        buffer.writeByte(0x21)  // negative int -2 (0x21 = -1 - 1)
        writeByteString(buffer, x)

        // -3 (y) -> ByteString(32)
        buffer.writeByte(0x22)  // negative int -3 (0x22 = -1 - 2)
        writeByteString(buffer, y)

        return buffer.readByteArray()
    }

    private fun writeByteString(buffer: Buffer, bytes: ByteArray) {
        // Byte string de 32 bytes (0x58 0x20)
        buffer.writeByte(0x58)  // byte string, 1-byte length
        buffer.writeByte(bytes.size)
        buffer.write(bytes)
    }

    private fun getPublicKeyFromByteArray(bytes: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(bytes)
        val keyFactory = KeyFactory.getInstance("EC")
        return keyFactory.generatePublic(keySpec)
    }

    private fun ByteArray.padTo32Bytes(): ByteArray {
        return if (this.size == 32) {
            this
        } else if (this.size > 32) {
            this.copyOfRange(this.size - 32, this.size)
        } else {
            ByteArray(32 - this.size) { 0x00 } + this
        }
    }
}
