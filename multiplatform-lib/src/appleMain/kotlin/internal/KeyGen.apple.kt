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
import com.infomaniak.auth.lib.extensions.tryIt
import com.infomaniak.auth.lib.internal.extensions.firstOrElse
import com.infomaniak.auth.lib.internal.utils.Xor
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.kCFAllocatorDefault
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
import platform.Security.kSecAttrKeyType
import platform.Security.kSecAttrKeyTypeECSECPrimeRandom
import platform.Security.kSecAttrTokenID
import platform.Security.kSecAttrTokenIDSecureEnclave
import platform.Security.kSecPrivateKeyAttrs
import platform.Security.kSecPublicKeyAttrs

/**
 * Generates an EC private key in the KeyChain for future use.
 *
 * A public key can be generated later from it.
 */
internal fun generateEcPrivateKeyInTheKeychain(
    tag: String,
    privateKeyPurposes: KeyPurposes = KeyPurposes.privateKeyDefaults,
    publicKeyPurposes: KeyPurposes? = null,
    keyAccessGuard: KeyAccessGuard,
    accessibility: KeyAccessibility
): Xor<SecKeyRef, NSError> = generatePrivateKey(
    tag = tag,
    privateKeyPurposes = privateKeyPurposes,
    publicKeyPurposes = publicKeyPurposes,
    keyAccessGuard = keyAccessGuard,
    accessibility = accessibility,
    storageLocation = KeyStorageLocation.KeyChain
)

/**
 * Generates an EC private key in the device Secure Enclave for future use.
 *
 * A public key can be generated later from it.
 *
 * See [Apple doc](https://developer.apple.com/documentation/security/protecting-keys-with-the-secure-enclave?language=objc)
 */
internal fun generatePrivateKeyInTheSecureEnclave(
    tag: String,
    privateKeyPurposes: KeyPurposes = KeyPurposes.privateKeyDefaults,
    publicKeyPurposes: KeyPurposes? = null,
    keyAccessGuard: KeyAccessGuard,
    accessibility: KeyAccessibility.SecureEnclaveCompatible // Don't allow wrong accessibility flags.
): Xor<SecKeyRef, NSError> = generatePrivateKey(
    tag = tag,
    privateKeyPurposes = privateKeyPurposes,
    publicKeyPurposes = publicKeyPurposes,
    keyAccessGuard = keyAccessGuard,
    accessibility = accessibility,
    storageLocation = KeyStorageLocation.SecureEnclave
)

/**
 * Generates an EC private key.
 *
 * A public key can be generated from it.
 */
internal fun generateEcPrivateKeyInMemory(
    tag: String,
    privateKeyPurposes: KeyPurposes = KeyPurposes.privateKeyDefaults,
    publicKeyPurposes: KeyPurposes? = null,
    keyAccessGuard: KeyAccessGuard,
    accessibility: KeyAccessibility
): Xor<SecKeyRef, NSError> = generatePrivateKey(
    tag = tag,
    privateKeyPurposes = privateKeyPurposes,
    publicKeyPurposes = publicKeyPurposes,
    keyAccessGuard = keyAccessGuard,
    accessibility = accessibility,
    storageLocation = null
)

private fun generatePrivateKey(
    tag: String,
    privateKeyPurposes: KeyPurposes = KeyPurposes.privateKeyDefaults,
    publicKeyPurposes: KeyPurposes? = null,
    keyAccessGuard: KeyAccessGuard,
    accessibility: KeyAccessibility,
    storageLocation: KeyStorageLocation?,
): Xor<SecKeyRef, NSError> {
    val attributes = createKeyAttributes(
        tag = tag,
        privateKeyPurposes = privateKeyPurposes,
        publicKeyPurposes = publicKeyPurposes,
        accessControl = keyAccessGuard,
        accessibility = accessibility,
        storageLocation = storageLocation,
    )

    return tryIt { e -> SecKeyCreateRandomKey(attributes, e) }
}

private fun createKeyAttributes(
    tag: String,
    privateKeyPurposes: KeyPurposes?,
    publicKeyPurposes: KeyPurposes?,
    accessControl: KeyAccessGuard,
    accessibility: KeyAccessibility,
    storageLocation: KeyStorageLocation?,
) = buildCFDictionary {
    // See https://developer.apple.com/documentation/security/generating-new-cryptographic-keys#Creating-an-Asymmetric-Key-Pair
    // See all key gen attributes here: https://developer.apple.com/documentation/security/key-generation-attributes
    this[kSecAttrKeyType] = kSecAttrKeyTypeECSECPrimeRandom
    this[kSecAttrKeySizeInBits] = 256
    when (storageLocation) {
        KeyStorageLocation.SecureEnclave -> {
            this[kSecAttrTokenID] = kSecAttrTokenIDSecureEnclave
        }
        KeyStorageLocation.KeyChain, null -> {}
    }
    this[kSecPrivateKeyAttrs] = buildCFDictionary {
        privateKeyPurposes?.applyTo(this)
        this[kSecAttrIsPermanent] = storageLocation != null
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
        this[kSecAttrIsPermanent] = storageLocation != null
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
    accessControl: KeyAccessGuard,
    accessibility: KeyAccessibility
) = tryIt { errorPointer ->
    SecAccessControlCreateWithFlags(
        allocator = kCFAllocatorDefault,
        protection = accessibility.toKSecAttrAccessible(),
        flags = accessControl.toAccessControlFlags(),
        error = errorPointer
    )
}.firstOrElse { error -> throw Exception("Error creating access control: $error") }
