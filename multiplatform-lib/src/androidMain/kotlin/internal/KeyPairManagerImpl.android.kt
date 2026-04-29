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
import kotlinx.io.IOException
import splitties.init.appCtx
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

internal actual fun createKeyPairManager(): KeyPairManager = KeyPairManagerAndroidImpl()

private class KeyPairManagerAndroidImpl : KeyPairManager() {

    private val keysDir by lazy {
        appCtx.filesDir.resolve("passkeys").also { passkeysDir ->
            passkeysDir.mkdir()
            //TODO[ik-auth]: Remove the code below after the next pre-release.
            appCtx.filesDir.listFiles { it.name.endsWith(".key") }!!.forEach { keyFile ->
                keyFile.renameTo(passkeysDir.resolve(keyFile.name))
            }
        }
    }

    @Throws(Exception::class)
    override suspend fun generateNewKey(userId: Long, keyId: String): Failure.KeyManagement.GenerationFailed? {
        val keyPair = generateEcKeyPair().getOrElse {
            return Failure.KeyManagement.GenerationFailed(it.toString())
        }

        Dispatchers.IO {
            keyFile(userId = userId, keyId = keyId, isPublic = false).writeBytes(keyPair.private.encoded)
            keyFile(userId = userId, keyId = keyId, isPublic = true).writeBytes(keyPair.public.encoded)
        }
        return null
    }

    override suspend fun retrievePublicKey(
        userId: Long,
        keyId: String,
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> = Dispatchers.IO {
        val file = keyFile(userId = userId, keyId = keyId, isPublic = true)
        runCatching {
            Xor.First(file.readBytes())
        }.getOrElse { Xor.Second(Failure.KeyManagement.KeyExtractionFailed(it.toString())) }
    }

    override suspend fun retrievePrivateKey(
        userId: Long,
        keyId: String,
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> = Dispatchers.IO {
        val file = keyFile(userId = userId, keyId = keyId, isPublic = false)
        runCatching {
            Xor.First(file.readBytes())
        }.getOrElse { Xor.Second(Failure.KeyManagement.KeyExtractionFailed(it.toString())) }
    }

    override suspend fun getSortedKeyIds(matchOn: MatchOn): List<String> {
        val files = withContext(Dispatchers.IO) {
            keysDir.listFiles()
        } ?: return emptyList()
        return buildList {
            val predicate = matchOn.asFilterPredicate()
            for (file in files) {
                val fileName = file.name
                if (predicate(file.name)) {
                    val fileTimestampMillis = Dispatchers.IO {
                        try {
                            Files.readAttributes(file.toPath(), BasicFileAttributes::class.java).creationTime().toMillis()
                        } catch (_: IOException) {
                            file.lastModified()
                        }
                    }
                    add(extractKeyIdFromFileName(fileName) to fileTimestampMillis)
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
            keysDir.listFiles()
        }?.find {
            predicate(it.name)
        } ?: return null

        return extractKeyIdFromFileName(userPassKey.name)
    }

    override suspend fun deleteKeysMatching(matchOn: MatchOn): Xor<Unit, Failure.KeyManagement.KeyNotFound> {
        val predicate = matchOn.asFilterPredicate()
        val keys = withContext(Dispatchers.IO) {
            keysDir.listFiles()
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

    private fun keyFile(userId: Long, keyId: String, isPublic: Boolean): File {
        val visibility = if (isPublic) "public" else "private"
        return keysDir.resolve("$userId-$keyId-$visibility.key")
    }
}
