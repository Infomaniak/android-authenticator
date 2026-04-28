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
package com.infomaniak.auth.lib.internal

import com.infomaniak.auth.lib.internal.utils.Xor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.withContext
import splitties.init.appCtx
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

internal actual fun KeyPairManager(): KeyPairManager = KeyPairManagerAndroidImpl()

private class KeyPairManagerAndroidImpl : KeyPairManager {

    @Throws(Exception::class)
    override suspend fun generateNewKey(userId: Long, keyId: String): Failure.KeyManagement.GenerationFailed? {
        val keyPair = generateEcKeyPair().getOrElse {
            return Failure.KeyManagement.GenerationFailed(it.toString())
        }

        saveFileToFilesDir("$userId-$keyId-private.key", keyPair.private.encoded)
        saveFileToFilesDir("$userId-$keyId-public.key", keyPair.public.encoded)
        return null
    }

    override suspend fun retrievePublicKey(
        userId: Long,
        keyId: String,
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> = Dispatchers.IO {
        val file = File(appCtx.filesDir, "$userId-$keyId-public.key")
        runCatching {
            Xor.First(file.readBytes())
        }.getOrElse { Xor.Second(Failure.KeyManagement.KeyExtractionFailed(it.toString())) }
    }

    override suspend fun retrievePrivateKey(
        userId: Long,
        keyId: String,
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> = Dispatchers.IO {
        val file = File(appCtx.filesDir, "$userId-$keyId-private.key")
        runCatching {
            Xor.First(file.readBytes())
        }.getOrElse { Xor.Second(Failure.KeyManagement.KeyExtractionFailed(it.toString())) }
    }

    override suspend fun getSortedKeyIds(predicate: (name: String) -> Boolean): List<String> {
        val files = withContext(Dispatchers.IO) {
            appCtx.filesDir.listFiles()
        } ?: return emptyList()
        return buildList {
            for (file in files) {
                val fileName = file.name
                if (predicate(file.name)) {
                    val keyId = fileName.substring(
                        startIndex = fileName.indexOfFirst { it == '-' } + 1,
                        endIndex = fileName.indexOfLast { it == '-' }
                    )
                    val attrs = Dispatchers.IO { Files.readAttributes(file.toPath(), BasicFileAttributes::class.java) }
                    add(keyId to attrs.creationTime())
                }
            }
        }.sortedBy { (_, creationTime) -> creationTime }.map { (keyId, _) -> keyId }
    }

    override suspend fun findKeyIdFor(predicate: (name: String) -> Boolean): String? {
        val userPassKey: File = withContext(Dispatchers.IO) {
            appCtx.filesDir.listFiles()
        }?.find {
            predicate(it.name)
        } ?: return null

        val keyId = userPassKey.name.substring(
            startIndex = userPassKey.name.indexOfFirst { it == '-' } + 1,
            endIndex = userPassKey.name.indexOfLast { it == '-' }
        )
        return keyId
    }

    override suspend fun deleteKeysMatching(predicate: (name: String) -> Boolean): Xor<Unit, Failure.KeyManagement.KeyNotFound> {
        val keys = withContext(Dispatchers.IO) {
            appCtx.filesDir.listFiles()
        }?.filter {
            predicate(it.name)
        } ?: return Xor.Second(Failure.KeyManagement.KeyNotFound("No keys"))

        keys.forEach { it.delete() }

        return Xor.First(Unit)
    }

    private suspend fun saveFileToFilesDir(fileName: String, key: ByteArray) = Dispatchers.IO {
        val file = File(appCtx.filesDir, fileName)
        file.writeBytes(key)
    }
}
