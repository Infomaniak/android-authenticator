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

import com.infomaniak.auth.lib.extensions.buildCFDictionary
import com.infomaniak.auth.lib.extensions.set
import com.infomaniak.auth.lib.extensions.tryIt
import com.infomaniak.auth.lib.internal.AsnOneTypes
import com.infomaniak.auth.lib.internal.encodeAsn1Integer
import com.infomaniak.auth.lib.internal.firstOrElse
import com.infomaniak.auth.lib.internal.toByteArray
import com.infomaniak.auth.lib.internal.toCFDataRef
import com.infomaniak.auth.lib.internal.toNSData
import com.infomaniak.auth.lib.internal.utils.trimOrPadStart
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFRelease
import platform.Security.SecKeyCreateSignature
import platform.Security.SecKeyCreateWithData
import platform.Security.SecKeyRef
import platform.Security.SecKeyVerifySignature
import platform.Security.kSecAttrKeyClass
import platform.Security.kSecAttrKeyClassPrivate
import platform.Security.kSecAttrKeyClassPublic
import platform.Security.kSecAttrKeySizeInBits
import platform.Security.kSecAttrKeyType
import platform.Security.kSecAttrKeyTypeECSECPrimeRandom
import platform.Security.kSecKeyAlgorithmECDSASignatureMessageX962SHA256

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
internal actual object SignUtils {

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

    actual fun verifySignature(publicKey: ByteArray, data: ByteArray, signatureData: ByteArray): Boolean {
        val attributes = buildCFDictionary {
            this[kSecAttrKeyType] = kSecAttrKeyTypeECSECPrimeRandom
            this[kSecAttrKeyClass] = kSecAttrKeyClassPublic
            this[kSecAttrKeySizeInBits] = 256
        }
        val key = tryIt { errorPtr ->
            SecKeyCreateWithData(publicKey.toNSData().toCFDataRef(), attributes, errorPtr)
        }.firstOrElse { println(it); return false }
        return tryIt { errorPtr ->
            SecKeyVerifySignature(
                key = key,
                algorithm = kSecKeyAlgorithmECDSASignatureMessageX962SHA256,
                signedData = data.toNSData().toCFDataRef(),
                signature = signatureData.toNSData().toCFDataRef(),
                error = errorPtr
            )
        }.firstOrElse { println(it); return false }
    }

    private fun convertX962ToDer(x962Signature: ByteArray): ByteArray {
        val (r, s) = convertDerToRawSignature(x962Signature)

        val sequenceContent = r.encodeAsn1Integer() + s.encodeAsn1Integer()

        val sequenceLength = sequenceContent.size
        return if (sequenceLength <= 127) {
            byteArrayOf(AsnOneTypes.SEQUENCE, sequenceLength.toByte()) + sequenceContent
        } else {
            val lengthBytes = byteArrayOf(
                (sequenceLength shr 8).toByte(),
                sequenceLength.toByte()
            )
            byteArrayOf(AsnOneTypes.SEQUENCE, 0x82.toByte()) + lengthBytes + sequenceContent
        }
    }

    private fun convertDerToRawSignature(derSignature: ByteArray): Pair<ByteArray, ByteArray> {
        require(derSignature.size in 70..72) {
            "Invalid DER signature length: ${derSignature.size}, expected 70-72"
        }

        // Position after SEQUENCE header (0x30, length)
        var pos = 2

        // Parse INTEGER r
        require(derSignature[pos] == AsnOneTypes.INTEGER) { "Expected INTEGER tag for r" }
        pos++
        val rLen = derSignature[pos].toInt() and 0xFF
        pos++
        val r = derSignature.copyOfRange(pos, pos + rLen).trimOrPadStart(32)
        pos += rLen

        // Parse INTEGER s
        require(derSignature[pos] == AsnOneTypes.INTEGER) { "Expected INTEGER tag for s" }
        pos++
        val sLen = derSignature[pos].toInt() and 0xFF
        pos++
        val s = derSignature.copyOfRange(pos, pos + sLen).trimOrPadStart(32)

        return r to s
    }

    private fun importPrivateKeyFromBytes(keyBytes: ByteArray): SecKeyRef? = memScoped {
        val keyData = keyBytes.toNSData().toCFDataRef()

        val attributes = buildCFDictionary {
            this[kSecAttrKeyType] set kSecAttrKeyTypeECSECPrimeRandom
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
