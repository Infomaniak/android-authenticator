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

import com.google.android.gms.auth.blockstore.BlockstoreClient
import com.infomaniak.core.auth.backup.BlockStore
import com.infomaniak.core.common.cancellable
import com.infomaniak.core.sentry.SentryLog
import com.infomaniak.multiplatform_authenticator.core.PasskeysStorageLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File

object BlockStoreBackup {
    private val blockStore = BlockStore.instance
    private const val PASSKEYS_KEY = "pk"
    private const val TAG = "BlockStoreBackup"

    suspend fun backupPasskeys(): Boolean {
        val backupContent = dumpPasskeys()
        val alreadyBackedUpContent = readPasskeysBackup()
        val bytes = ProtoBuf.encodeToByteArray(backupContent)
        if (alreadyBackedUpContent != null && bytes contentEquals ProtoBuf.encodeToByteArray(alreadyBackedUpContent)) return true
        return writePasskeysBackup(bytes)
    }

    suspend fun restorePasskeys() {
        val passKeysBackup = readPasskeysBackup() ?: return
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
                // shouldBackupToCloud = blockStore.isE2eeAvailable(),
            return false
        }
        return runCatching {
            blockStore.storeBytes(
                key = PASSKEYS_KEY,
                shouldBackupToCloud = true,
                bytes = protobufEncodedBytes
            )
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

    private suspend fun readPasskeysBackup(): PasskeysBackup? {
        val bytes = blockStore.retrieveBytes(listOf(PASSKEYS_KEY))[PASSKEYS_KEY]
            ?: return null
        return ProtoBuf.decodeFromByteArray<PasskeysBackup>(bytes)
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

    private data class KeyPairReference(
        val userId: Long,
        val keyId: String,
    )
}
