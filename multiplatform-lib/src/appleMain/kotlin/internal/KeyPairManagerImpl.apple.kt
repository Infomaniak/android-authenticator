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
import com.infomaniak.auth.lib.internal.extensions.get
import com.infomaniak.auth.lib.internal.extensions.set
import com.infomaniak.auth.lib.internal.extensions.size
import com.infomaniak.auth.lib.internal.extensions.toByteArray
import com.infomaniak.auth.lib.internal.extensions.toNSData
import com.infomaniak.auth.lib.internal.extensions.toNSDate
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
import platform.CoreFoundation.CFArrayRef
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDateRef
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.timeIntervalSince1970
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecKeyCopyExternalRepresentation
import platform.Security.SecKeyCopyPublicKey
import platform.Security.SecKeyRef
import platform.Security.errSecSuccess
import platform.Security.kSecAttrApplicationTag
import platform.Security.kSecAttrCreationDate
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

internal actual fun createKeyPairManager(): KeyPairManager = KeyPairManagerAppleImpl()

private class KeyPairManagerAppleImpl : KeyPairManager() {

    override suspend fun generateNewKey(
        userId: Long,
        keyId: String,
    ): Failure.KeyManagement.GenerationFailed? = Dispatchers.IO {

        val result = generateEcPrivateKeyInTheKeychain(
            tag = "$userId-$keyId",
            privateKeyPurposes = KeyPurposes.privateKeyDefaults,
            publicKeyPurposes = KeyPurposes.publicKeyDefaults,
            keyAccessGuard = KeyAccessGuard.Unguarded,
            accessibility = KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly,
        )
        when (result) {
            is Xor.First -> result.value.use { null }
            is Xor.Second -> Failure.KeyManagement.GenerationFailed(result.value.toString())
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun retrievePublicKey(
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

    override suspend fun retrievePrivateKey(
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

    override suspend fun getSortedKeyIds(matchOn: MatchOn): List<String> = Dispatchers.IO {
        memScoped {
            val (resultsArray, count) = getAllPrivateKeysQuery()
            if (resultsArray == null || count == 0) return@memScoped emptyList()
            val predicate = matchOn.asFilterPredicate()

            buildList {
                for (i in 0 until count) {
                    val item: CFDictionaryRef = resultsArray[i.toLong()]
                    val tag = extractTagFromItem(item) ?: continue
                    val dateRef: CFDateRef = item[kSecAttrCreationDate]

                    if (predicate(tag)) {
                        add(extractKeyIdFromTag(tag) to dateRef.toNSDate())
                    }
                }
            }.sortedBy { (_, date) ->
                date.timeIntervalSince1970
            }.map { (keyId, _) ->
                keyId
            }.distinct() // Private/public keys pairs have a common id, so we filter duplicates.
        }
    }

    @OptIn(BetaInteropApi::class)
    override suspend fun findKeyIdFor(matchOn: MatchOn): String? = Dispatchers.IO {
        memScoped {
            val (resultsArray, count) = getAllPrivateKeysQuery()

            if (resultsArray == null || count == 0) return@memScoped null
            val predicate = matchOn.asFilterPredicate()

            for (i in 0 until count) {
                val tag = extractTagFromItem(resultsArray[i.toLong()]) ?: continue

                if (predicate(tag)) return@memScoped extractKeyIdFromTag(tag)
            }

            return@memScoped null
        }
    }

    override suspend fun deleteKeysMatching(matchOn: MatchOn): Xor<Unit, Failure.KeyManagement.KeyNotFound> = Dispatchers.IO {
        memScoped {
            val (resultsArray, count) = getAllPrivateKeysQuery()

            if (resultsArray == null || count == 0) {
                return@memScoped Xor.Second(Failure.KeyManagement.KeyNotFound("No keys found in Keychain"))
            }
            val predicate = matchOn.asFilterPredicate()

            var hasDeletedAtLeastOneKey = false
            for (i in 0 until count) {
                val tag = extractTagFromItem(resultsArray[i.toLong()]) ?: continue

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
    }

    override fun MatchOn.PasskeyId.asFilterPredicate(): (String) -> Boolean {
        val publicKeyEnd = "$id.pub"
        return { name: String -> name.endsWith(id) || name.endsWith(publicKeyEnd) }
    }

    private fun extractKeyIdFromTag(tag: String): String = tag.substring(startIndex = tag.indexOfFirst { it == '-' } + 1)

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
            Pair(resultsArray, resultsArray.size.toInt())
        } else {
            Pair(null, 0)
        }
    }

    private fun extractTagFromItem(item: CFDictionaryRef?): String? {
        val tagData: CFDataRef = item[kSecAttrApplicationTag] ?: return null
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
