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

internal actual fun createKeyPairManager(): KeyPairManager = KeyPairManagerAndroidImpl()

private class KeyPairManagerAndroidImpl : KeyPairManager() {

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

    override suspend fun getSortedKeyIds(matchOn: MatchOn): List<String> {
        val files = withContext(Dispatchers.IO) {
            appCtx.filesDir.listFiles()
        } ?: return emptyList()
        return buildList {
            val predicate = matchOn.asFilterPredicate()
            for (file in files) {
                val fileName = file.name
                if (predicate(file.name)) {
                    val attrs = Dispatchers.IO { Files.readAttributes(file.toPath(), BasicFileAttributes::class.java) }
                    add(extractKeyIdFromFileName(fileName) to attrs.creationTime())
                }
            }
        }.sortedBy { (_, creationTime) ->
            creationTime
        }.map { (keyId, _) ->
            keyId
        }.distinct() // Private/public keys pairs have a common id, so we filter duplicates.
    }

    override suspend fun findKeyIdFor(matchOn: MatchOn): String? {
        val predicate = matchOn.asFilterPredicate()
        val userPassKey: File = withContext(Dispatchers.IO) {
            appCtx.filesDir.listFiles()
        }?.find {
            predicate(it.name)
        } ?: return null

        //TODO 2: Put keys into a dedicated dir
        return extractKeyIdFromFileName(userPassKey.name)
    }

    override suspend fun deleteKeysMatching(matchOn: MatchOn): Xor<Unit, Failure.KeyManagement.KeyNotFound> {
        val predicate = matchOn.asFilterPredicate()
        val keys = withContext(Dispatchers.IO) {
            appCtx.filesDir.listFiles()
        }?.filter {
            predicate(it.name)
        } ?: return Xor.Second(Failure.KeyManagement.KeyNotFound("No keys"))

        keys.forEach { it.delete() }

        return Xor.First(Unit)
    }

    override fun MatchOn.PasskeyId.asFilterPredicate() = { name: String -> "-$id-" in name }

    private fun extractKeyIdFromFileName(name: String): String = name.substring(
        startIndex = name.indexOfFirst { it == '-' } + 1,
        endIndex = name.indexOfLast { it == '-' }
    )

    private suspend fun saveFileToFilesDir(fileName: String, key: ByteArray) = Dispatchers.IO {
        val file = File(appCtx.filesDir, fileName)
        file.writeBytes(key)
    }
}
