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
@file:OptIn(ExperimentalSerializationApi::class)

package com.infomaniak.auth.backup

import com.google.android.gms.auth.blockstore.Blockstore
import com.google.android.gms.auth.blockstore.BlockstoreClient
import com.google.android.gms.auth.blockstore.RetrieveBytesRequest
import com.google.android.gms.auth.blockstore.StoreBytesData
import com.infomaniak.core.common.cancellable
import com.infomaniak.core.sentry.SentryLog
import com.infomaniak.multiplatform_authenticator.core.PasskeysStorageLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import splitties.init.appCtx
import java.io.File

object BlockStoreBackup {
    private val blockstoreClient = Blockstore.getClient(appCtx)
    private const val PASSKEYS_KEY = "pk"
    private const val TAG = "BlockStoreBackup"

    suspend fun backupPasskeys(): Boolean {
        val backupContent = dumpPasskeys()
        val alreadyBackedUpContent = readPasskeysBackup()
        val bytes = ProtoBuf.encodeToByteArray(backupContent)
        if (bytes contentEquals ProtoBuf.encodeToByteArray(alreadyBackedUpContent)) return true
        return writePasskeysBackup(bytes)
    }

    suspend fun restorePasskeys() {
        val passKeysBackup = readPasskeysBackup()
        applyPasskeysBackup(passKeysBackup)
    }

    private suspend fun writePasskeysBackup(protobufEncodedBytes: ByteArray): Boolean {
        val keySizeInBytes = PASSKEYS_KEY.toByteArray().size
        val contentSizeInBytes = protobufEncodedBytes.size
        val entireSize = contentSizeInBytes + keySizeInBytes
        if (entireSize > BlockstoreClient.MAX_SIZE) {
            SentryLog.e(TAG, "Too many passkeys to backup") { scope ->
                scope.setExtra("Max size", "${BlockstoreClient.MAX_SIZE}B")
                scope.setExtra("Actual size", "${keySizeInBytes}B + ${contentSizeInBytes}B = ${entireSize}B")
            }
            return false
        }
        val storeRequest = StoreBytesData.Builder()
            .setKey(PASSKEYS_KEY)
            .setShouldBackupToCloud(true)
            .setBytes(protobufEncodedBytes)
            .build()
        return runCatching {
            blockstoreClient.storeBytes(storeRequest).await()
            true
        }.cancellable().getOrElse { throwable ->
            SentryLog.wtf(TAG, "Failed to backup passkeys", throwable)
            false
        }
    }

    private suspend fun dumpPasskeys(): PasskeysBackup = Dispatchers.IO {
        PasskeysBackup(
            entries = keyRefs().map { keyPairRef ->
                PasskeysBackup.PasskeyEntry(
                    keyPairRef.userId,
                    keyPairRef.keyId,
                    private = keyFile(keyPairRef, isPublic = false).readBytes(),
                    public = keyFile(keyPairRef, isPublic = true).readBytes(),
                )
            }
        )
    }

    private suspend fun readPasskeysBackup(): PasskeysBackup {
        val retrieveRequest = RetrieveBytesRequest.Builder()
            .setKeys(listOf(PASSKEYS_KEY))
            .build()
        val bytes = blockstoreClient.retrieveBytes(retrieveRequest).await().blockstoreDataMap[PASSKEYS_KEY]!!.bytes
        return ProtoBuf.decodeFromByteArray(bytes)
    }

    private suspend fun applyPasskeysBackup(backup: PasskeysBackup) {
        Dispatchers.IO {
            backup.entries.forEach { entry ->
                val keyPairRef = KeyPairReference(entry.userId, entry.keyId)
                keyFile(keyPairRef, isPublic = false).writeBytes(entry.private)
                keyFile(keyPairRef, isPublic = true).writeBytes(entry.public)
            }
        }
    }

    private suspend fun keyRefs(): List<KeyPairReference> = Dispatchers.IO {
        PasskeysStorageLocation.dir.list().orEmpty()
    }.mapNotNull { fileName ->
        val userIdAndKeyId = fileName
            .substringBefore(
                delimiter = ".key",
                missingDelimiterValue = ""
            )
            .ifEmpty { return@mapNotNull null }
            .substringBeforeLast('-')
        KeyPairReference(
            userId = userIdAndKeyId.substringBefore('-').toLong(),
            keyId = userIdAndKeyId.substringAfter('-')
        )
    }.distinct()

    private fun keyFile(reference: KeyPairReference, isPublic: Boolean): File = PasskeysStorageLocation.keyFile(
        userId = reference.userId,
        keyId = reference.keyId,
        isPublic = isPublic,
    )

    private suspend fun isE2eeAvailable(): Boolean = blockstoreClient.isEndToEndEncryptionAvailable.await()

    private class KeyPairReference(
        val userId: Long,
        val keyId: String,
    )
}
