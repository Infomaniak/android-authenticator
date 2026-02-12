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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import splitties.init.appCtx
import java.io.File


internal class KeyPairManagerImpl : KeyPairManager {

    @Throws(Exception::class)
    override suspend fun generateNewKey() {
        val keyPair = generateEcKeyPair().getOrThrow()

        saveKeyToFilesDir(PRIVATE_KEY_NAME, keyPair.private.encoded)
        saveKeyToFilesDir(PUBLIC_KEY_NAME, keyPair.public.encoded)
    }

    override suspend fun retrievePublicKey(): ByteArray = Dispatchers.IO {
        val file = File(appCtx.filesDir, PUBLIC_KEY_NAME)
        file.readBytes()
    }

    private suspend fun saveKeyToFilesDir(fileName: String, key: ByteArray) = Dispatchers.IO {
        val file = File(appCtx.filesDir, fileName)
        file.writeBytes(key)
    }

    companion object {
        private const val PUBLIC_KEY_NAME = "public.key"
        private const val PRIVATE_KEY_NAME = "private.key"
    }
}
