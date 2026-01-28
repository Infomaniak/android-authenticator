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

package com.infomaniak.auth.lib.internal

import com.infomaniak.auth.lib.extensions.buildCFDictionary
import com.infomaniak.auth.lib.extensions.set
import com.infomaniak.auth.lib.extensions.toNsData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFErrorRefVar
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.kCFAllocatorDefault
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSError
import platform.Security.SecAccessControlCreateWithFlags
import platform.Security.SecKeyCreateRandomKey
import platform.Security.SecKeyRef
import platform.Security.kSecAttrAccessControl
import platform.Security.kSecAttrApplicationTag
import platform.Security.kSecAttrCanDecrypt
import platform.Security.kSecAttrCanDerive
import platform.Security.kSecAttrCanEncrypt
import platform.Security.kSecAttrCanSign
import platform.Security.kSecAttrCanUnwrap
import platform.Security.kSecAttrCanVerify
import platform.Security.kSecAttrCanWrap
import platform.Security.kSecAttrIsPermanent
import platform.Security.kSecAttrKeySizeInBits
import platform.Security.kSecAttrKeyTypeECSECPrimeRandom
import platform.Security.kSecAttrTokenID
import platform.Security.kSecAttrTokenIDSecureEnclave
import platform.Security.kSecAttrType
import platform.Security.kSecPrivateKeyAttrs
import platform.Security.kSecPublicKeyAttrs

/**
 * Generates an EC private key in the device Secure Enclave for future use.
 *
 * A public key can be generated later from it.
 */
internal fun generatePrivateKeyInTheSecureEnclave(
    tag: String,
    privateKeyPurposes: KeyPurposes = KeyPurposes.privateKeyDefaults,
    publicKeyPurposes: KeyPurposes? = null,
    accessControl: KeyAccessControl,
    accessibility: KeyAccessibility.SecureEnclaveCompatible
): Xor<SecKeyRef, NSError> = memScoped {
    val attributes = createKeyAttributes(
        tag = tag,
        privateKeyPurposes = privateKeyPurposes,
        publicKeyPurposes = publicKeyPurposes,
        accessControl = accessControl,
        accessibility = accessibility,
    )

    val e = alloc<CFErrorRefVar>()

    val privateKey: SecKeyRef? = SecKeyCreateRandomKey(attributes, e.ptr)

    // The cast below is fine because it's a "toll-free bridged" type.
    // See Apple doc archive on it:
    // https://developer.apple.com/library/archive/documentation/CoreFoundation/Conceptual/CFDesignConcepts/Articles/tollFreeBridgedTypes.html#//apple_ref/doc/uid/TP40010677
    when (val error = CFBridgingRelease(e.value) as NSError?) {
        null -> Xor.First(privateKey!!)
        else -> Xor.Second(error)
    }
}

private fun createKeyAttributes(
    tag: String,
    privateKeyPurposes: KeyPurposes?,
    publicKeyPurposes: KeyPurposes?,
    accessControl: KeyAccessControl,
    accessibility: KeyAccessibility,
) = buildCFDictionary {
    // See https://developer.apple.com/documentation/security/generating-new-cryptographic-keys#Creating-an-Asymmetric-Key-Pair
    // See all key gen attributes here: https://developer.apple.com/documentation/security/key-generation-attributes
    this[kSecAttrType] = kSecAttrKeyTypeECSECPrimeRandom
    this[kSecAttrKeySizeInBits] = 256
    this[kSecAttrTokenID] = kSecAttrTokenIDSecureEnclave
    this[kSecPrivateKeyAttrs] = buildCFDictionary {
        privateKeyPurposes?.applyTo(this)
        this[kSecAttrIsPermanent] = true
        this[kSecAttrAccessControl] = createAccessControl(
            accessControl = accessControl,
            accessibility = accessibility,
        )
        // No need to specify kSecAttrAccessible since it's already set in kSecAttrAccessControl just above.
        /*
         * You also specify the kSecAttrApplicationTag attribute with a unique NSData value
         * so that you can find and retrieve it from the keychain later.
         * The tag data is constructed from a string, using reverse DNS notation,
         * though any unique tag will do.
         */
        this[kSecAttrApplicationTag] = tag.toNsData()
    }
    if (publicKeyPurposes != null) this[kSecPublicKeyAttrs] = buildCFDictionary {
        this[kSecAttrIsPermanent] = true
        publicKeyPurposes.applyTo(this)
        this[kSecAttrApplicationTag] = "$tag.pub".toNsData()
    }
}

private fun KeyPurposes.applyTo(dictionary: CFMutableDictionaryRef?) {
    dictionary[kSecAttrCanSign] = signing
    dictionary[kSecAttrCanVerify] = verifying

    dictionary[kSecAttrCanEncrypt] = encrypting
    dictionary[kSecAttrCanDecrypt] = decrypting

    dictionary[kSecAttrCanDerive] = deriving

    dictionary[kSecAttrCanWrap] = wrapping
    dictionary[kSecAttrCanUnwrap] = unwrapping
}

private fun createAccessControl(
    accessControl: KeyAccessControl,
    accessibility: KeyAccessibility
) = memScoped {
    val e = alloc<CFErrorRefVar>()
    val access = SecAccessControlCreateWithFlags(
        allocator = kCFAllocatorDefault,
        protection = accessibility.toKSecAttrAccessible(),
        flags = accessControl.toAccessControlFlags(),
        error = e.ptr
    )
    when (val error = CFBridgingRelease(e.value) as NSError?) {
        null -> access!!
        else -> throw Exception("Error creating access control: $error")
    }
}
