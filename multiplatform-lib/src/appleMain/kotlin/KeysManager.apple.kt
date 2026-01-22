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

import cnames.structs.__CFError
import cnames.structs.__SecKey
import com.infomaniak.auth.lib.extensions.set
import com.infomaniak.auth.lib.extensions.size
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.readValues
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryGetCount
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSError
import platform.Security.SecAccessControlCreateWithFlags
import platform.Security.SecAccessControlRef
import platform.Security.SecKeyCreateRandomKey
import platform.Security.kSecAccessControlPrivateKeyUsage
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAlways
import platform.Security.kSecAttrApplicationTag
import platform.Security.kSecAttrCanSign
import platform.Security.kSecAttrCanVerify
import platform.Security.kSecAttrIsPermanent
import platform.Security.kSecAttrKeySizeInBits
import platform.Security.kSecAttrKeyTypeECSECPrimeRandom
import platform.Security.kSecAttrType
import platform.posix.err

class KeysManagerImpl : KeysManager {
    override suspend fun supportsTrustedExecutionEnvironment(): Boolean {
        TODO()
    }

    override suspend fun hasEnrolledBiometrics(): Boolean {
        TODO()
    }

    override suspend fun generateSecurelyStoredKeyPairForSigning(securityParameters: KeysManager.SecurityParameters) {
        TODO()
    }

    override suspend fun updateSecurityParameters(newParameters: KeysManager.SecurityParameters) {
        TODO()
    }

    override suspend fun createAndStoreMigrationKeys() {
        TODO()
    }

    override suspend fun getPublicKey(): PublicKey {
        TODO()
    }

    override suspend fun getMostRecentMigrationPublicKey(): PublicKey {
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
    // Convert the alias ByteArray to a CPointer to UByte for the key tag
    val tag = alias.toCValues().ptr

    // Create a CFDictionary with the key generation attributes.
    // This is equivalent to the KeyGenParameterSpec in Android.
    val attributes = CFDictionaryCreateMutable(
        allocator = kCFAllocatorDefault,
        capacity = 8, // We will add 8 key-value pairs
        keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
        valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr
    )

    attributes.also {
        it[kSecAttrType] = kSecAttrKeyTypeECSECPrimeRandom
        it[kSecAttrKeySizeInBits] = 256
        it[kSecAttrIsPermanent] = true
        // it[kSecAttrApplicationTag] = tag
        it[kSecAttrAccessible] = kSecAttrAccessibleAlways
        it[kSecAttrCanSign] = true
        it[kSecAttrCanVerify] = true
    }

    println("attributes.size = ${attributes.size}")

    val errorPtr = allocPointerTo<__CFError>()
    val error = errorPtr.value


    // Call the native function to generate the key pair
    // val private: SecAccessControlRef? = SecAccessControlCreateWithFlags(
    //     allocator = kCFAllocatorDefault,
    //     protection = kSecAttrAccessibleAlways,
    //     flags = kSecAccessControlPrivateKeyUsage,
    //     error = cValuesOf(error).ptr
    // )
    val privateKey = SecKeyCreateRandomKey(attributes, errorPtr.readValues(1))
    // SecKeyCreateRandomKey(attributes, cValuesOf(error).ptr)
    val theError = CFBridgingRelease(errorPtr.value) as NSError?
    println("Result? ${privateKey != null}")
    println("Error? ${error != null}")
    println("theError? ${theError != null}")
    return privateKey
}
