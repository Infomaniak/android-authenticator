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

import com.infomaniak.auth.lib.internal.webauthn.PublicKeyXY
import com.infomaniak.auth.lib.internal.webauthn.keyCoseOf
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyCoseTest {

    @Test
    fun testKeyCoseOfResult() {
        // Arbitrary binary data for testing
        val x = ByteArray(32) { it.toByte() }
        val y = ByteArray(32) { (it + 32).toByte() }

        // Check that the keyCoseOf function, using kotlinx.serialization yields the same result as our former manual approach.

        val publicKeyCose = keyCoseOf(x = x, y = y)
        val manualResult = encodeKeyCoseManually(PublicKeyXY(x, y))

        assertEquals(
            expected = publicKeyCose.map { it.toHexString() },
            actual = manualResult.map { it.toHexString() }
        )
    }
}

private fun encodeKeyCoseManually(publicKeyXY: PublicKeyXY): ByteArray {
    return Buffer().apply {
        // Map with 5 elements
        writeByte(0xA5)

        // 1 (kty) -> 2 (EC2)
        writeByte(0x01)  // unsigned int 1
        writeByte(0x02)  // unsigned int 2

        // 3 (alg) -> -7 (ES256)
        writeByte(0x03)  // unsigned int 3
        writeByte(0x26)  // negative int -7 (0x26 = -1 - 6)

        // -1 (crv) -> 1 (P-256)
        writeByte(0x20)  // negative int -1 (0x20 = -1 - 0)
        writeByte(0x01)  // unsigned int 1


        // -2 (x) -> ByteString(32)
        writeByte(0x21)  // negative int -2 (0x21 = -1 - 1)
        writeByteString(publicKeyXY.x)

        // -3 (y) -> ByteString(32)
        writeByte(0x22)  // negative int -3 (0x22 = -1 - 2)
        writeByteString(publicKeyXY.y)
    }.readByteArray()
}

private fun Buffer.writeByteString(bytes: ByteArray) {
    // Byte string of 32 bytes (0x58 0x20)
    writeByte(0x58)  // byte string, 1-byte length
    writeByte(bytes.size)
    write(bytes)
}
