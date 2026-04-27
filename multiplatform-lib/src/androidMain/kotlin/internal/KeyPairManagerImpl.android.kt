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

internal actual class KeyPairManagerImpl : KeyPairManager {

    @Throws(Exception::class)
    actual override suspend fun generateNewKey(userId: Long, keyId: String): Failure.KeyManagement.GenerationFailed? {
        val keyPair = generateEcKeyPair().getOrElse {
            return Failure.KeyManagement.GenerationFailed(it.toString())
        }

        saveFileToFilesDir("$userId-$keyId-private.key", keyPair.private.encoded)
        saveFileToFilesDir("$userId-$keyId-public.key", keyPair.public.encoded)
        return null
    }

    actual override suspend fun retrievePublicKey(
        userId: Long,
        keyId: String,
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> = Dispatchers.IO {
        val file = File(appCtx.filesDir, "$userId-$keyId-public.key")
        runCatching {
            Xor.First(file.readBytes())
        }.getOrElse { Xor.Second(Failure.KeyManagement.KeyExtractionFailed(it.toString())) }
    }

    actual override suspend fun retrievePrivateKey(
        userId: Long,
        keyId: String,
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed> = Dispatchers.IO {
        val file = File(appCtx.filesDir, "$userId-$keyId-private.key")
        runCatching {
            Xor.First(file.readBytes())
        }.getOrElse { Xor.Second(Failure.KeyManagement.KeyExtractionFailed(it.toString())) }
    }

    actual override suspend fun findKeyIdFor(predicate: (name: String) -> Boolean): Xor<String, Failure.KeyManagement.KeyNotFound> {
        val userPassKey: File = withContext(Dispatchers.IO) {
            appCtx.filesDir.listFiles()
        }?.find {
            predicate(it.name)
        } ?: return Xor.Second(Failure.KeyManagement.KeyNotFound("No keys"))

        val keyId = userPassKey.name.substring(
            startIndex = userPassKey.name.indexOfFirst { it == '-' } + 1,
            endIndex = userPassKey.name.indexOfLast { it == '-' }
        )
        return Xor.First(keyId)
    }

    actual override suspend fun deleteKeysMatching(predicate: (name: String) -> Boolean): Xor<Unit, Failure.KeyManagement.KeyNotFound> {
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
