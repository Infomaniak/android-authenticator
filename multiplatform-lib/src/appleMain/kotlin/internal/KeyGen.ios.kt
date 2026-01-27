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
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSError
import platform.Security.SecKeyCreateRandomKey
import platform.Security.SecKeyRef
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
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

internal data class Purposes(
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

    sealed interface AfterFirstUnlock : KeyAccessibility {
        companion object : AfterFirstUnlock

        data object ThisDeviceOnly : AfterFirstUnlock, SecureEnclaveCompatible
    }

}

/**
 * Generates an EC key pair in the iOS Keychain for future use.
 */
internal fun generatePrivateKeyInTheSecureEnclave(
    tag: String,
    purposes: Purposes,
    accessibility: KeyAccessibility.SecureEnclaveCompatible,
): Xor<SecKeyRef, NSError> = memScoped {
    val attributes = createKeyAttributes(tag, purposes, accessibility)

    val e = alloc<CFErrorRefVar>()

    val privateKey: SecKeyRef? = SecKeyCreateRandomKey(attributes, e.ptr)

    when (val error = CFBridgingRelease(e.value) as NSError?) {
        null -> Xor.First(privateKey!!)
        else -> Xor.Second(error)
    }
}

private fun KeyAccessibility.toKSecAttrAccessible() {
    when (this) {
        KeyAccessibility.AfterFirstUnlock -> kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly -> kSecAttrAccessibleAfterFirstUnlock
    }
}

private fun createKeyAttributes(
    tag: String,
    purposes: Purposes,
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
    this[kSecAttrAccessible] = kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly

    if (purposes.signing) this[kSecAttrCanSign] = true
    if (purposes.verifying) this[kSecAttrCanVerify] = true

    if (purposes.encrypting) this[kSecAttrCanEncrypt] = true
    if (purposes.decrypting) this[kSecAttrCanDecrypt] = true

    if (purposes.deriving) this[kSecAttrCanDerive] = true

    if (purposes.wrapping) this[kSecAttrCanWrap] = true
    if (purposes.unwrapping) this[kSecAttrCanUnwrap] = true
}
