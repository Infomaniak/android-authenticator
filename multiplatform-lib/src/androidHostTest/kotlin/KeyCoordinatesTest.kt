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

import com.infomaniak.auth.lib.internal.AsnOneTypes
import com.infomaniak.auth.lib.internal.generateEcKeyPair
import com.infomaniak.auth.lib.internal.utils.getKeyCoordinates
import com.infomaniak.auth.lib.internal.utils.keyCoordinatesOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyCoordinatesTest {

    @Test
    fun `test generated extracted coordinates are of the right size`() = runTest {
        val publicKeyData = generateEcKeyPair().getOrThrow().public.encoded
        println("key: ${publicKeyData.toHexString()}")
        val (x, y) = getKeyCoordinates(publicKeyData)
        println("x: ${x.toHexString()}")
        println("y: ${y.toHexString()}")
        assertEquals(expected = 32, actual = x.size)
        assertEquals(expected = 32, actual = y.size)
    }

    @Test
    fun `test generated key coordinates manual and API powered extraction match`() = runTest {
        val publicKeyData = generateEcKeyPair().getOrThrow().public.encoded
        `check Java Security API and manual x509 key coordinates extraction match`(publicKeyData)
    }

    @Test
    fun `test hardcoded key coordinates manual and API powered extraction match`() = runTest {
        val publicKeyData = x509key.hexToByteArray()
        `check Java Security API and manual x509 key coordinates extraction match`(publicKeyData)
    }

    private fun `check Java Security API and manual x509 key coordinates extraction match`(x509Key: ByteArray) {
        println("x509Key: ${x509Key.toHexString()}")
        val (x, y) = getKeyCoordinates(x509Key)
        val (manualX, manualY) = getKeyCoordinatesFromX509Key(x509Key)
        println("x: ${x.toHexString()}")
        println("y: ${y.toHexString()}")
        assertEquals(expected = x.toHexString(), actual = manualX.toHexString())
        assertEquals(expected = y.toHexString(), actual = manualY.toHexString())
    }

    @Test
    fun `test X509 key coordinates extraction`() = runTest {
        val publicKeyData = x509key.hexToByteArray()
        println("key: ${publicKeyData.toHexString()}")
        val (x, y) = getKeyCoordinatesFromX509Key(publicKeyData)
        println("x: ${x.toHexString()}")
        println("y: ${y.toHexString()}")
        assertEquals(expected = x509keyX, actual = x.toHexString())
        assertEquals(expected = x509keyY, actual = y.toHexString())
    }

    @Test
    fun `test iOS-generated key coordinates extraction`() = runTest {
        val publicKeyData = iosGeneratedP256Key.hexToByteArray()
        println("key(size = ${publicKeyData.size}): ${publicKeyData.toHexString()}")
        assertEquals(expected = 65, actual = publicKeyData.size)
        val (x, y) = keyCoordinatesOf(uncompressedP256Key = publicKeyData)
        println("x: ${x.toHexString()}")
        println("y: ${y.toHexString()}")
        assertEquals(expected = iosGeneratedP256KeyX, actual = x.toHexString())
        assertEquals(expected = iosGeneratedP256KeyY, actual = y.toHexString())
    }
}

// iOS Generates P256 uncompressed keys (65 bytes long). The first byte is a "marker". The remaining bytes are the 2 coordinates.
private const val iosGeneratedP256Key =
    "04294dbea2e9f02fc4884f31de5f2db9986b976e51cb71011efdabaca9e42ee50af1d1db7e11194f006ab1ba9610815bb63e04d9f532a1ca998bdc8b16dc3a9b28"
private const val iosGeneratedP256KeyX = "294dbea2e9f02fc4884f31de5f2db9986b976e51cb71011efdabaca9e42ee50a"
private const val iosGeneratedP256KeyY = "f1d1db7e11194f006ab1ba9610815bb63e04d9f532a1ca998bdc8b16dc3a9b28"

// Android Generates x509 keys. There are APIs in java.security (X509EncodedKeySpec and ECPublicKey) to extract the key coords.
private const val x509key =
    "3059301306072a8648ce3d020106082a8648ce3d0301070342000400735909928aa144938662fc225059415ba2bfad884540e7e332949ef0f483d68024795a478fededd856f3823721f210bc0f31648f732959459a48bb5c5a3959"
private const val x509keyX = "00735909928aa144938662fc225059415ba2bfad884540e7e332949ef0f483d6"
private const val x509keyY = "8024795a478fededd856f3823721f210bc0f31648f732959459a48bb5c5a3959"

private fun getKeyCoordinatesFromX509Key(key: ByteArray): PublicKeyXY {
    val uncompressedKey = parseX509SubjectPublicKeyInfoIntoCompressedP256UncompressedKey(key)
    require(uncompressedKey[0] == 0x04.toByte()) { "Expected uncompressed format" }
    require(uncompressedKey.size == 65) { "Invalid key length: ${uncompressedKey.size}" }

    val x = uncompressedKey.copyOfRange(1, 33)
    val y = uncompressedKey.copyOfRange(33, 65)

    return PublicKeyXY(x, y)
}

private fun parseX509SubjectPublicKeyInfoIntoCompressedP256UncompressedKey(bytes: ByteArray): ByteArray {
    var offset = 0

    // SubjectPublicKeyInfo SEQUENCE
    require(bytes[offset++] == AsnOneTypes.SEQUENCE)
    val seqLength = readAsn1Length(bytes, offset)
    offset += getLengthBytes(seqLength)

    // AlgorithmIdentifier SEQUENCE
    require(bytes[offset++] == AsnOneTypes.SEQUENCE)
    val algoIdLength = readAsn1Length(bytes, offset)
    offset += getLengthBytes(algoIdLength)
    offset += algoIdLength // Also skip the algorithm identifier sub-sequence

    // BIT STRING
    require(bytes[offset++] == AsnOneTypes.BIT_STRING)
    val bitStringLength = readAsn1Length(bytes, offset)
    offset += getLengthBytes(bitStringLength)

    // Skip unused bits
    offset++

    return bytes.copyOfRange(offset, bytes.size)
}

/**
 * ASN.1 (Abstract Syntax Notation One)
 */
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
