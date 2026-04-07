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

import com.infomaniak.auth.lib.internal.extensions.buildCFDictionary
import com.infomaniak.auth.lib.internal.extensions.set
import com.infomaniak.auth.lib.internal.extensions.toByteArray
import com.infomaniak.auth.lib.internal.extensions.toNSData
import com.infomaniak.auth.lib.internal.extensions.toNsData
import com.infomaniak.auth.lib.internal.extensions.tryIt
import com.infomaniak.auth.lib.internal.extensions.use
import com.infomaniak.auth.lib.internal.utils.Xor
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.invoke
import platform.CoreFoundation.CFArrayGetCount
import platform.CoreFoundation.CFArrayGetValueAtIndex
import platform.CoreFoundation.CFArrayRef
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDictionaryGetValue
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
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
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitAll
import platform.Security.kSecReturnAttributes
import platform.Security.kSecReturnRef

internal actual class KeyPairManagerImpl : KeyPairManager {

    actual override suspend fun generateNewKey(
        userId: Long,
        keyId: String,
    ): Failure.KeyManagement.GenerationFailed? = Dispatchers.IO {

        val result = generateEcPrivateKeyInTheKeychain(
            tag = "$userId-$keyId",
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
        userId: Long,
        keyId: String,
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> = Dispatchers.IO {
        memScoped {
            // Get private key to retrieve public key
            getPrivateKeyRef("$userId-$keyId").use { privateKeyRef ->
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
        userId: Long,
        keyId: String
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> {
        memScoped {
            getPrivateKeyRef("$userId-$keyId").use { privateKeyRef ->
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

    @OptIn(BetaInteropApi::class)
    actual override suspend fun findKeyIdFor(userId: Long): Xor<String, Failure.KeyManagement.KeyNotFound> = memScoped {
        //TODO[ik-auth]: Test this code somehow.
        val userIdPrefix = "$userId-"
        val (resultsArray, count) = getAllPrivateKeysQuery()

        if (resultsArray == null || count == 0) {
            return@memScoped Xor.Second(Failure.KeyManagement.KeyNotFound("No keys found in Keychain"))
        }

        for (i in 0 until count) {
            val tag = extractTagFromItem(CFArrayGetValueAtIndex(resultsArray, i.toLong()))

            if (tag?.startsWith(userIdPrefix) == true) {
                val keyId = tag.removePrefix(userIdPrefix)
                return@memScoped Xor.First(keyId)
            }
        }

        Xor.Second(Failure.KeyManagement.KeyNotFound("No key found for userId $userId"))
    }

    actual override suspend fun deleteKeysMatching(predicate: (name: String) -> Boolean): Xor<Unit, Failure.KeyManagement.KeyNotFound> =
        memScoped {
            val (resultsArray, count) = getAllPrivateKeysQuery()

            if (resultsArray == null || count == 0) {
                return@memScoped Xor.Second(Failure.KeyManagement.KeyNotFound("No keys found in Keychain"))
            }

            var hasDeletedAtLeastOneKey = false
            for (i in 0 until count) {
                val tag = extractTagFromItem(CFArrayGetValueAtIndex(resultsArray, i.toLong())) ?: continue

                if (predicate(tag)) {
                    deleteKeyByTag(tag)
                    hasDeletedAtLeastOneKey = true
                }
            }

            if (hasDeletedAtLeastOneKey) {
                Xor.First(Unit)
            } else {
                Xor.Second(Failure.KeyManagement.KeyNotFound("No key containing $predicate"))
            }
        }

    @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
    private fun MemScope.getAllPrivateKeysQuery(): Pair<CFArrayRef?, Int> {
        val query = buildCFDictionary {
            this[kSecClass] = kSecClassKey
            this[kSecAttrKeyClass] = kSecAttrKeyClassPrivate
            this[kSecAttrKeyType] = kSecAttrKeyTypeECSECPrimeRandom
            this[kSecReturnAttributes] = true
            this[kSecMatchLimit] = kSecMatchLimitAll
        }

        val resultRef = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, resultRef.ptr)
        CFRelease(query)

        return if (status == errSecSuccess && resultRef.value != null) {
            @Suppress("unchecked_cast")
            val resultsArray = resultRef.value as CFArrayRef
            Pair(resultsArray, CFArrayGetCount(resultsArray).toInt())
        } else {
            Pair(null, 0)
        }
    }

    @OptIn(BetaInteropApi::class)
    private fun extractTagFromItem(item: CFTypeRef?): String? {
        @Suppress("unchecked_cast")
        val tagData = CFDictionaryGetValue(item as CFDictionaryRef, kSecAttrApplicationTag) as? CFDataRef ?: return null
        return tagData.toNSData().toByteArray().decodeToString()
    }

    private fun deleteKeyByTag(tag: String) {
        val deleteQuery = buildCFDictionary {
            this[kSecClass] = kSecClassKey
            this[kSecAttrApplicationTag] = tag.toNsData()
        }
        SecItemDelete(deleteQuery)
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun MemScope.getPrivateKeyRef(keyAlias: String): SecKeyRef {
        val query = buildCFDictionary {
            this[kSecAttrKeyType] = kSecAttrKeyTypeECSECPrimeRandom
            this[kSecAttrKeyClass] = kSecAttrKeyClassPrivate
            this[kSecClass] = kSecClassKey
            this[kSecAttrApplicationTag] = keyAlias.toNsData()
            this[kSecReturnRef] = true
        }

        val privateKeyRefVar = alloc<CFTypeRefVar>()
        val resultStatus = Dispatchers.IO { SecItemCopyMatching(query, privateKeyRefVar.ptr) }

        if (resultStatus != errSecSuccess || privateKeyRefVar.value == null) {
            throw Exception("Failed to retrieve private key from KeyChain (error: $resultStatus)")
        }

        CFRelease(query)

        @Suppress("UNCHECKED_CAST")
        return privateKeyRefVar.value as SecKeyRef
    }
}
