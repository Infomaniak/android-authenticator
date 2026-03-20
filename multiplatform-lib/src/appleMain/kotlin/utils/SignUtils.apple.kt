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
@file:OptIn(ExperimentalForeignApi::class)

package com.infomaniak.auth.lib.utils

import com.infomaniak.auth.lib.extensions.buildCFDictionary
import com.infomaniak.auth.lib.extensions.set
import com.infomaniak.auth.lib.extensions.toByteArray
import com.infomaniak.auth.lib.extensions.toCFDataRef
import com.infomaniak.auth.lib.extensions.toNSData
import com.infomaniak.auth.lib.extensions.tryIt
import com.infomaniak.auth.lib.internal.Xor
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFRelease
import platform.Security.SecKeyCreateSignature
import platform.Security.SecKeyCreateWithData
import platform.Security.SecKeyRef
import platform.Security.kSecAttrKeyClass
import platform.Security.kSecAttrKeyClassPrivate
import platform.Security.kSecAttrKeySizeInBits
import platform.Security.kSecAttrKeyType
import platform.Security.kSecAttrKeyTypeECSECPrimeRandom
import platform.Security.kSecKeyAlgorithmECDSASignatureMessageX962SHA256

@OptIn(BetaInteropApi::class)
actual object SignUtils {

    actual fun signWithPrivateKey(privateKey: ByteArray, data: ByteArray): ByteArray = memScoped {
        val privateKeyRef = importPrivateKeyFromBytes(privateKey)
            ?: throw IllegalArgumentException("Failed to import private key")

        val dataToSign = data.toNSData().toCFDataRef()

        val signatureResult = tryIt { errorPtr ->
            SecKeyCreateSignature(
                key = privateKeyRef,
                algorithm = kSecKeyAlgorithmECDSASignatureMessageX962SHA256,
                dataToSign = dataToSign,
                error = errorPtr
            )
        }

        CFRelease(privateKeyRef)

        //TODO[Authenticator]: Check the code below works properly, with SignUtilsTest.

        when (signatureResult) {
            is Xor.First -> convertX962ToDer(signatureResult.value.toByteArray())
            is Xor.Second -> throw IllegalStateException("Signing failed: ${signatureResult.value.localizedDescription}")
        }
    }

    private fun convertX962ToDer(x962Signature: ByteArray): ByteArray {
        val rawSignature = convertDerToRawSignature(x962Signature)

        require(rawSignature.size == 64) { "Signature must be exactly 64 bytes. Actual size: ${rawSignature.size}" }

        val r = rawSignature.copyOfRange(0, 32)
        val s = rawSignature.copyOfRange(32, 64)

        fun trimLeadingZeros(bytes: ByteArray): ByteArray {
            var i = 0
            while (i < bytes.size - 1 && bytes[i] == 0.toByte()) i++
            return bytes.copyOfRange(i, bytes.size)
        }

        val rTrimmed = trimLeadingZeros(r)
        val sTrimmed = trimLeadingZeros(s)

        fun encodeAsn1Integer(value: ByteArray): ByteArray {
            val needsPadding = value[0].toInt() and 0x80 != 0
            val length = value.size + if (needsPadding) 1 else 0
            val result = ByteArray(2 + length)

            result[0] = 0x02 // INTEGER tag
            result[1] = length.toByte()

            if (needsPadding) {
                result[2] = 0x00
                value.copyInto(result, 3)
            } else {
                value.copyInto(result, 2)
            }

            return result
        }

        val rEncoded = encodeAsn1Integer(rTrimmed)
        val sEncoded = encodeAsn1Integer(sTrimmed)
        val sequenceContent = rEncoded + sEncoded

        val sequenceLength = sequenceContent.size
        return if (sequenceLength <= 127) {
            byteArrayOf(0x30, sequenceLength.toByte()) + sequenceContent
        } else {
            val lengthBytes = byteArrayOf(
                (sequenceLength shr 8).toByte(),
                sequenceLength.toByte()
            )
            byteArrayOf(0x30.toByte(), 0x82.toByte()) + lengthBytes + sequenceContent
        }
    }

    private fun convertDerToRawSignature(derSignature: ByteArray): ByteArray {
        require(derSignature.size in 70..72) {
            "Invalid DER signature length: ${derSignature.size}, expected 70-72"
        }

        // Position after SEQUENCE header (0x30, length)
        var pos = 2

        // Parse INTEGER r
        require(derSignature[pos] == 0x02.toByte()) { "Expected INTEGER tag for r" }
        pos++
        val rLen = derSignature[pos].toInt() and 0xFF
        pos++
        val r = derSignature.copyOfRange(pos, pos + rLen).let {
            // Enlever le padding 0x00 si présent (indicateur de signe positif ASN.1)
            if (it.size == 33 && it[0] == 0x00.toByte()) it.copyOfRange(1, 33) else it
        }
        pos += rLen

        // Parse INTEGER s
        require(derSignature[pos] == 0x02.toByte()) { "Expected INTEGER tag for s" }
        pos++
        val sLen = derSignature[pos].toInt() and 0xFF
        pos++
        val s = derSignature.copyOfRange(pos, pos + sLen).let {
            // Enlever le padding 0x00 si présent
            if (it.size == 33 && it[0] == 0x00.toByte()) it.copyOfRange(1, 33) else it
        }

        require(r.size == 32 && s.size == 32) {
            "Invalid r/s length: r=${r.size}, s=${s.size}, expected 32/32"
        }

        return r + s
    }

    private fun importPrivateKeyFromBytes(keyBytes: ByteArray): SecKeyRef? = memScoped {
        val keyData = keyBytes.toNSData().toCFDataRef()

        val attributes = buildCFDictionary {
            this[kSecAttrKeyType] = kSecAttrKeyTypeECSECPrimeRandom
            this[kSecAttrKeyClass] = kSecAttrKeyClassPrivate
            this[kSecAttrKeySizeInBits] = 256
        }

        val privateKey = tryIt { errorPtr ->
            SecKeyCreateWithData(
                keyData = keyData,
                attributes = attributes,
                error = errorPtr
            )
        }

        CFRelease(attributes)

        return when (privateKey) {
            is Xor.First -> privateKey.firstOrNull()
            is Xor.Second -> {
                println("Error importing key: ${privateKey.value.localizedDescription}")
                null
            }
        }
    }

    private fun CFDataRef.toByteArray(): ByteArray = this.toNSData().toByteArray()
}
