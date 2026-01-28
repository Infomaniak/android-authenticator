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
import platform.CoreFoundation.CFStringRef
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSError
import platform.Security.SecKeyCreateRandomKey
import platform.Security.SecKeyRef
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccessibleAlways
import platform.Security.kSecAttrAccessibleAlwaysThisDeviceOnly
import platform.Security.kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly
import platform.Security.kSecAttrAccessibleWhenUnlocked
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrApplicationTag
import platform.Security.kSecAttrCanDecrypt
import platform.Security.kSecAttrCanDerive
import platform.Security.kSecAttrCanEncrypt
import platform.Security.kSecAttrCanSign
import platform.Security.kSecAttrCanUnwrap
import platform.Security.kSecAttrCanVerify
import platform.Security.kSecAttrCanWrap
import platform.Security.kSecAttrKeySizeInBits
import platform.Security.kSecAttrKeyTypeECSECPrimeRandom
import platform.Security.kSecAttrType
import platform.Security.kSecPrivateKeyAttrs

internal data class KeyPurposes(
    val signing: Boolean = false,
    val deriving: Boolean = false,
    val verifying: Boolean = false,
    val encrypting: Boolean = false,
    val decrypting: Boolean = false,
    val wrapping: Boolean = false,
    val unwrapping: Boolean = false,
)

internal sealed interface KeyAccessibility {
    sealed interface SecureEnclaveCompatible : KeyAccessibility

    sealed interface WhenUnlocked : KeyAccessibility {
        companion object : WhenUnlocked
        data object ThisDeviceOnly : WhenUnlocked, SecureEnclaveCompatible
    }

    sealed interface AfterFirstUnlock : KeyAccessibility {
        companion object : AfterFirstUnlock
        data object ThisDeviceOnly : AfterFirstUnlock, SecureEnclaveCompatible
    }

    sealed interface WhenPasscodeSet : KeyAccessibility {
        data object ThisDeviceOnly : WhenPasscodeSet, SecureEnclaveCompatible
    }

    sealed interface Always : KeyAccessibility {
        companion object : Always
        data object ThisDeviceOnly : Always, SecureEnclaveCompatible
    }
}

/**
 * Generates an EC private key in the device Secure Enclave for future use.
 *
 * A public key can be generated later from it.
 */
internal fun generatePrivateKeyInTheSecureEnclave(
    tag: String,
    purposes: KeyPurposes,
    accessibility: KeyAccessibility.SecureEnclaveCompatible,
): Xor<SecKeyRef, NSError> = memScoped {
    val attributes = createKeyAttributes(tag, purposes, accessibility)

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

private fun KeyAccessibility.toKSecAttrAccessible(): CFStringRef? = when (this) {
    KeyAccessibility.WhenUnlocked.ThisDeviceOnly -> kSecAttrAccessibleWhenUnlockedThisDeviceOnly
    KeyAccessibility.WhenUnlocked -> kSecAttrAccessibleWhenUnlocked
    KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly -> kSecAttrAccessibleAfterFirstUnlock
    KeyAccessibility.AfterFirstUnlock -> kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
    KeyAccessibility.WhenPasscodeSet.ThisDeviceOnly -> kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly
    KeyAccessibility.Always -> kSecAttrAccessibleAlways
    KeyAccessibility.Always.ThisDeviceOnly -> kSecAttrAccessibleAlwaysThisDeviceOnly
}

private fun createKeyAttributes(
    tag: String,
    purposes: KeyPurposes,
    accessibility: KeyAccessibility,
) = buildCFDictionary {
    // See https://developer.apple.com/documentation/security/generating-new-cryptographic-keys#Creating-an-Asymmetric-Key-Pair
    // See all key gen attributes here: https://developer.apple.com/documentation/security/key-generation-attributes
    this[kSecAttrType] = kSecAttrKeyTypeECSECPrimeRandom
    this[kSecAttrKeySizeInBits] = 256
    this[kSecPrivateKeyAttrs] = buildCFDictionary {
        /*
         * You also specify the kSecAttrApplicationTag attribute with a unique NSData value
         * so that you can find and retrieve it from the keychain later.
         * The tag data is constructed from a string, using reverse DNS notation,
         * though any unique tag will do.
         */
        this[kSecAttrApplicationTag] = tag.toNsData()
    }
    this[kSecAttrAccessible] = accessibility.toKSecAttrAccessible()

    if (purposes.signing) this[kSecAttrCanSign] = true
    if (purposes.verifying) this[kSecAttrCanVerify] = true

    if (purposes.encrypting) this[kSecAttrCanEncrypt] = true
    if (purposes.decrypting) this[kSecAttrCanDecrypt] = true

    if (purposes.deriving) this[kSecAttrCanDerive] = true

    if (purposes.wrapping) this[kSecAttrCanWrap] = true
    if (purposes.unwrapping) this[kSecAttrCanUnwrap] = true
}
