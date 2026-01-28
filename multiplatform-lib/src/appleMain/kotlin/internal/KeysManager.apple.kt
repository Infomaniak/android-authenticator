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
        val keyPurposes = KeyPurposes(
            signing = true,
            verifying = true,
        )
        generateKey(whichOne = KeyReference.BasicOperations, purposes = keyPurposes)
        generateKey(whichOne = KeyReference.BasicOperations, purposes = keyPurposes)
        TODO()
    }

    //TODO[iOS-KeyChain]: Find keys with https://developer.apple.com/documentation/security/secitemcopymatching(_:_:)?language=objc
    //TODO[iOS-Secure-Enclave]: https://developer.apple.com/documentation/security/protecting-keys-with-the-secure-enclave?language=objc

}

@Throws(Exception::class)
private fun generateKey(whichOne: KeyReference.HardwareSecured, purposes: KeyPurposes): SecKeyRef {
    val result = generatePrivateKeyInTheSecureEnclave(
        tag = whichOne.toKeyTagOrAlias(),
        purposes = purposes,
        accessibility = accessibilityFor(whichOne)
    )
    when (result) {
        is Xor.First -> return result.value
        is Xor.Second -> {
            val errorMessage = result.value.let { it.description ?: it.localizedDescription }
            throw Exception("Error generating $whichOne key: $errorMessage")
        }
    }
}

private fun accessibilityFor(
    reference: KeyReference.HardwareSecured
): KeyAccessibility.SecureEnclaveCompatible = when (reference) {
    KeyReference.BasicOperations -> KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly
    is KeyReference.SensitiveOperations -> KeyAccessibility.WhenUnlocked.ThisDeviceOnly
}
