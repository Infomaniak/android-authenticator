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

import com.infomaniak.auth.lib.Failure
import com.infomaniak.auth.lib.extensions.buildCFDictionary
import com.infomaniak.auth.lib.extensions.set
import com.infomaniak.auth.lib.extensions.toByteArray
import com.infomaniak.auth.lib.extensions.toNSData
import com.infomaniak.auth.lib.extensions.toNsData
import com.infomaniak.auth.lib.extensions.tryIt
import com.infomaniak.auth.lib.extensions.use
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
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
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

internal actual class KeyPairManagerImpl : KeyPairManager {

    actual override suspend fun generateNewKey(userId: Int, keyId: String): Failure.KeyManagement.GenerationFailed? =
        Dispatchers.IO {
            val result = generateEcPrivateKeyInTheKeychain(
                tag = "$keyId-$userId",
                privateKeyPurposes = KeyPairManager.privateKeyPurposes,
                publicKeyPurposes = KeyPairManager.publicKeyPurposes,
                keyAccessGuard = KeyAccessGuard.Unguarded,
                accessibility = KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly,
            )
            when (result) {
                is Xor.First -> result.value.use { null }
                is Xor.Second -> Failure.KeyManagement.GenerationFailed(result.value.toString())
            }
        }

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun retrievePublicKey(
        userId: Int,
        keyId: String
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> = Dispatchers.IO {
        memScoped {
            // Get private key to retrieve public key
            getPrivateKeyRef().use { privateKeyRef ->
                SecKeyCopyPublicKey(privateKeyRef) ?: throw Exception("Failed to extract public key from private key")
            }.use { publicKeyRef ->

                val result = tryIt { errorPointer -> SecKeyCopyExternalRepresentation(publicKeyRef, errorPointer) }

                val publicKeyData = when (result) {
                    is Xor.First -> result.value.toNSData()
                    is Xor.Second -> return@IO Xor.Second(Failure.KeyManagement.KeyExtractionFailed(result.value.toString()))
                }

                Xor.First(publicKeyData.toByteArray())
            }
        }
    }

    actual override suspend fun retrievePrivateKey(
        userId: Int,
        keyId: String
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> {
        memScoped {
            getPrivateKeyRef().use { privateKeyRef ->
                val result = tryIt { errorPointer ->
                    SecKeyCopyExternalRepresentation(privateKeyRef, errorPointer)
                }

                return when (result) {
                    is Xor.First -> Xor.First(result.value.toNSData().toByteArray())
                    is Xor.Second -> Xor.Second(Failure.KeyManagement.KeyExtractionFailed(result.value.toString()))
                }
            }
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
