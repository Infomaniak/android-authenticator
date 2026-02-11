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
import com.infomaniak.auth.lib.internal.KeyPairManager.Companion.ALIAS
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.invoke
import platform.CoreFoundation.CFErrorRefVar
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSData
import platform.Security.SecItemCopyMatching
import platform.Security.SecKeyCopyExternalRepresentation
import platform.Security.SecKeyCopyPublicKey
import platform.Security.SecKeyRef
import platform.Security.errSecSuccess
import platform.Security.kSecAttrApplicationTag
import platform.Security.kSecAttrKeyClass
import platform.Security.kSecAttrKeyClassPrivate
import platform.Security.kSecAttrKeyType
import platform.Security.kSecAttrKeyTypeECSECPrimeRandom
import platform.Security.kSecClass
import platform.Security.kSecClassKey
import platform.Security.kSecReturnRef
import toByteArray

internal class KeyPairManagerImpl : KeyPairManager {

    override suspend fun generateNewKey(): Unit = Dispatchers.IO {
        val result = generateEcPrivateKeyInTheKeychain(
            tag = ALIAS,
            privateKeyPurposes = KeyPairManager.privateKeyPurposes,
            publicKeyPurposes = KeyPairManager.publicKeyPurposes,
            keyAccessGuard = KeyAccessGuard.Unguarded,
            accessibility = KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly,
        )
        when (result) {
            is Xor.First -> CFRelease(result.value)
            is Xor.Second -> {
                val errorMessage = result.value.let { it.description ?: it.localizedDescription }
                throw Exception("Error generating key: $errorMessage")
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun retrievePublicKey(): ByteArray = Dispatchers.IO {
        memScoped {
            // Get private and public key
            val privateKeyRef = getPrivateKeyRef()
            val publicKeyRef =
                SecKeyCopyPublicKey(privateKeyRef) ?: throw Exception("Failed to extract public key from private key")
            CFRelease(privateKeyRef)

            // Handling error
            val errorVar = alloc<CFErrorRefVar>()
            val publicKeyDataCF = SecKeyCopyExternalRepresentation(publicKeyRef, errorVar.ptr)

            val error = CFBridgingRelease(errorVar.value)
            if (error != null || publicKeyDataCF == null) {
                CFRelease(publicKeyRef)
                throw Exception("Failed to export public key as data: $error")
            }

            CFRelease(publicKeyRef)

            // Extract data from public key
            val publicKeyData = CFBridgingRelease(publicKeyDataCF) as NSData
            publicKeyData.toByteArray()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun MemScope.getPrivateKeyRef(): SecKeyRef {
        val query = buildCFDictionary {
            this[kSecAttrKeyType] = kSecAttrKeyTypeECSECPrimeRandom
            this[kSecAttrKeyClass] = kSecAttrKeyClassPrivate
            this[kSecClass] = kSecClassKey
            this[kSecAttrApplicationTag] = ALIAS.toNsData()
            this[kSecReturnRef] = true
        }

        val privateKeyRefVar = alloc<CFTypeRefVar>()
        val resultStatus = SecItemCopyMatching(query, privateKeyRefVar.ptr)

        if (resultStatus != errSecSuccess || privateKeyRefVar.value == null) {
            throw Exception("Failed to retrieve private key from KeyChain (error: $resultStatus)")
        }

        CFRelease(query)

        @Suppress("UNCHECKED_CAST")
        return privateKeyRefVar.value as SecKeyRef
    }
}
