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

import com.infomaniak.auth.lib.extensions.toByteArray
import com.infomaniak.auth.lib.extensions.toNSData
import com.infomaniak.auth.lib.extensions.tryIt
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFNumberCreate
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFNumberIntType
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.create
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

        val dataToSign = memScoped {
            val nsData = NSData.create(bytes = allocArrayOf(data), length = data.size.toULong())
            nsData as CFDataRef
        }

        val errorPtr = alloc<ObjCObjectVar<NSError?>>()

        val signature = tryIt { errorPtr ->
            SecKeyCreateSignature(
                key = privateKeyRef,
                algorithm = kSecKeyAlgorithmECDSASignatureMessageX962SHA256,
                dataToSign = dataToSign,
                error = errorPtr
            )
        }

        CFRelease(privateKeyRef)

        if (signature.firstOrNull() == null) {
            val error = errorPtr.value
            throw IllegalStateException("Signing failed: ${error?.localizedDescription}")
        }

        convertX962ToDer(signature.firstOrNull()!!.toByteArray())
    }

    private fun convertX962ToDer(x962Signature: ByteArray): ByteArray {
        require(x962Signature.size == 64) { "X.962 signature must be exactly 64 bytes" }

        val r = x962Signature.copyOfRange(0, 32)
        val s = x962Signature.copyOfRange(32, 64)

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

    private fun importPrivateKeyFromBytes(keyBytes: ByteArray): SecKeyRef? = memScoped {
        val keyData = keyBytes.toNSData()

        val attributes = CFDictionaryCreateMutable(
            kCFAllocatorDefault,
            0,
            null,
            null
        )

        CFDictionaryAddValue(
            attributes,
            kSecAttrKeyType,
            kSecAttrKeyTypeECSECPrimeRandom
        )

        CFDictionaryAddValue(
            attributes,
            kSecAttrKeyClass,
            kSecAttrKeyClassPrivate
        )

        val keySize = alloc<IntVar>().apply { value = 256 }
        CFDictionaryAddValue(
            attributes,
            kSecAttrKeySizeInBits,
            CFNumberCreate(kCFAllocatorDefault, kCFNumberIntType, keySize.ptr)
        )

        val errorPtr = alloc<ObjCObjectVar<NSError?>>()

        val privateKey = tryIt { errorPtr ->
            SecKeyCreateWithData(
                keyData = keyData as CFDataRef,
                attributes = attributes,
                error = errorPtr
            )
        }

        CFRelease(attributes)

        if (privateKey.firstOrNull() == null) {
            println("Error importing key: ${errorPtr.value?.localizedDescription}")
        }

        privateKey.firstOrNull()
    }

    private fun CFDataRef.toByteArray(): ByteArray = this.toNSData().toByteArray()
}
