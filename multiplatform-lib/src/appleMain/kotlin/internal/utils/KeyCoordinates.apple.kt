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
package com.infomaniak.auth.lib.internal.utils

import com.infomaniak.auth.lib.PublicKeyXY

internal actual fun getKeyCoordinates(key: ByteArray): PublicKeyXY {
    val uncompressedKey = parseX509SubjectPublicKeyInfo(key)
    require(uncompressedKey[0] == 0x04.toByte()) { "Expected uncompressed format" }
    require(uncompressedKey.size == 65) { "Invalid key length: ${uncompressedKey.size}" }

    val x = uncompressedKey.copyOfRange(1, 33).padEnd(32)
    val y = uncompressedKey.copyOfRange(33, 65).padEnd(32)

    return PublicKeyXY(x, y)
}

private fun parseX509SubjectPublicKeyInfo(bytes: ByteArray): ByteArray {
    var offset = 0

    require(bytes[offset++] == 0x30.toByte()) { "Expected SEQUENCE" }
    val seqLength = readAsn1Length(bytes, offset)
    offset += getLengthBytes(seqLength)

    // AlgorithmIdentifier SEQUENCE
    require(bytes[offset++] == 0x30.toByte()) { "Expected AlgorithmIdentifier" }
    val algoIdLength = readAsn1Length(bytes, offset)
    offset += getLengthBytes(algoIdLength) + algoIdLength

    // BIT STRING
    require(bytes[offset++] == 0x03.toByte()) { "Expected BIT STRING" }
    val bitStringLength = readAsn1Length(bytes, offset)
    offset += getLengthBytes(bitStringLength)

    // Skip unused bits
    offset++

    // Public Key
    require(bytes[offset++] == 0x04.toByte()) { "Expected OCTET STRING" }
    val octetLength = readAsn1Length(bytes, offset)
    offset += getLengthBytes(octetLength)

    return bytes.copyOfRange(offset, offset + octetLength)
}

private fun readAsn1Length(bytes: ByteArray, offset: Int): Int {
    val firstByte = bytes[offset].toInt() and 0xFF
    return if (firstByte and 0x80 == 0) {
        firstByte
    } else {
        val numBytes = firstByte and 0x7F
        var length = 0
        for (i in 1..numBytes) {
            length = (length shl 8) or (bytes[offset + i].toInt() and 0xFF)
        }
        length
    }
}

private fun getLengthBytes(length: Int): Int {
    return when {
        length < 0x80 -> 1
        length < 0x100 -> 2
        length < 0x10000 -> 3
        else -> 4
    }
}
