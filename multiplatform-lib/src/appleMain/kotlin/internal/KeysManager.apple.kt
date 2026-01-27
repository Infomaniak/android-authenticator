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

package com.infomaniak.auth.lib.internal

import cnames.structs.__SecKey
import com.infomaniak.auth.lib.PublicKey
import com.infomaniak.auth.lib.extensions.buildCFDictionary
import com.infomaniak.auth.lib.extensions.set
import com.infomaniak.auth.lib.extensions.size
import com.infomaniak.auth.lib.extensions.toNsData
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFErrorRefVar
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSError
import platform.Security.SecKeyCopyAttributes
import platform.Security.SecKeyCopyPublicKey
import platform.Security.SecKeyCreateRandomKey
import platform.Security.SecKeyGetBlockSize
import platform.Security.SecKeyRef
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrApplicationTag
import platform.Security.kSecAttrCanDerive
import platform.Security.kSecAttrCanSign
import platform.Security.kSecAttrCanVerify
import platform.Security.kSecAttrIsPermanent
import platform.Security.kSecAttrKeySizeInBits
import platform.Security.kSecAttrKeyTypeECSECPrimeRandom
import platform.Security.kSecAttrType
import platform.Security.kSecPrivateKeyAttrs
import kotlin.test.assertNotEquals

class KeysManagerImpl : KeysManager {

    override suspend fun generateNewKeys() {

        TODO()
    }
    //TODO[iOS-KeyChain]: Find keys with https://developer.apple.com/documentation/security/secitemcopymatching(_:_:)?language=objc
    //TODO[iOS-Secure-Enclave]: https://developer.apple.com/documentation/security/protecting-keys-with-the-secure-enclave?language=objc

}

/**
 * Generates an EC key pair in the iOS Keychain for signing and verification.
 *
 * @param alias A unique tag (as bytes) to identify the key pair.
 * @return The private key's reference, or null on failure.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun generateKeyPair(alias: ByteArray): CPointer<__SecKey>? = memScoped {

    // Create a CFDictionary with the key generation attributes.
    // This is equivalent to the KeyGenParameterSpec in Android.

    // See https://developer.apple.com/documentation/security/generating-new-cryptographic-keys#Creating-an-Asymmetric-Key-Pair
    val attributes = buildCFDictionary {
        // See all key gen attributes here: https://developer.apple.com/documentation/security/key-generation-attributes
        this[kSecAttrType] = kSecAttrKeyTypeECSECPrimeRandom
        this[kSecAttrKeySizeInBits] = 256
        this[kSecPrivateKeyAttrs] = buildCFDictionary {
        //     /*
        //      * You also specify the kSecAttrApplicationTag attribute with a unique NSData value
        //      * so that you can find and retrieve it from the keychain later.
        //      * The tag data is constructed from a string, using reverse DNS notation,
        //      * though any unique tag will do.
        //      */
            this[kSecAttrApplicationTag] = "tag".toNsData()
        }
        this[kSecAttrAccessible] = kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        this[kSecAttrCanSign] = true
        this[kSecAttrCanDerive] = true
        this[kSecAttrCanVerify] = true
    }

    println("attributes.size = ${attributes.size}")


    // Call the native function to generate the key pair
    // val private: SecAccessControlRef? = SecAccessControlCreateWithFlags(
    //     allocator = kCFAllocatorDefault,
    //     protection = kSecAttrAccessibleAlways,
    //     flags = kSecAccessControlPrivateKeyUsage,
    //     error = cValuesOf(error).ptr
    // )

    val e = alloc<CFErrorRefVar>()
    val privateKey: SecKeyRef? = SecKeyCreateRandomKey(attributes, e.ptr)
    val error = CFBridgingRelease(e.value) as NSError?
    check((privateKey == null) != (error == null))

    println("Result? ${privateKey != null}")
    privateKey?.let {
        println(it)
        println(SecKeyCopyAttributes(it))
        println(SecKeyCopyPublicKey(it))
        println(SecKeyGetBlockSize(it))
    }
    println("Error? ${error != null}")
    error?.let {
        println(it.localizedDescription)

    }
    return privateKey
}
