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

@file:Suppress("SameParameterValue")

package com.infomaniak.auth.lib

import com.infomaniak.auth.lib.internal.webauthn.createEncodedWebAuthnAttestationObject
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

class WebAuthnAttestationObjectTest {

    @Test
    fun testResultMatchesTheManualWay() {
        // Arbitrary binary data for testing
        val fmt = "lol - ah!"
        val authData = "Whatever".toByteArray()


        // Check that the kotlinx.serialization way yields the same result as our former manual approach.

        val serializationResult = createEncodedWebAuthnAttestationObject(fmt, authData)
        val manualResult = encodeWebAuthnAttestationObjectManually(fmt, authData)

        assertEquals(
            expected = serializationResult.map { it.toHexString() },
            actual = manualResult.map { it.toHexString() }
        )
    }


}

private fun encodeWebAuthnAttestationObjectManually(
    fmt: String,
    authData: ByteArray
): ByteArray {
    return Buffer().apply {

        // Map start
        writeByte(0xA2) // 2 items

        // "fmt": "none"
        writeText("fmt")
        writeText(fmt)

        // "authData": <bytes>
        writeText("authData")
        buffer.writeByteString(authData)
    }.readByteArray()
}

private fun Buffer.writeText(text: String) {
    val bytes = text.encodeToByteArray()
    writeByte(0x60 + bytes.size) // 0x60 = text string base
    write(bytes)
}

private fun Buffer.writeByteString(bytes: ByteArray) {
    when {
        bytes.size <= 23 -> {
            writeByte(0x40 + bytes.size) // 0x40 = byte string base
        }
        bytes.size <= 255 -> {
            writeByte(0x58) // byte string, 1-byte length follows
            writeByte(bytes.size)
        }
        bytes.size <= 65535 -> {
            writeByte(0x59) // byte string, 2-byte length follows
            writeByte(bytes.size shr 8)
            writeByte(bytes.size and 0xFF)
        }
        else -> {
            writeByte(0x5A) // byte string, 4-byte length follows
            writeInt(bytes.size)
        }
    }
    write(bytes)
}
