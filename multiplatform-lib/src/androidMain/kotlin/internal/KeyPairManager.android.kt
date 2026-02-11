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

import com.infomaniak.auth.lib.internal.KeyPairManager.Companion.ALIAS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import java.security.KeyStore

internal class KeyPairManagerImpl : KeyPairManager {

    @Throws(Exception::class)
    override suspend fun generateNewKey() {
        generateEcKeyPair(
            alias = ALIAS,
            privateKeyPurposes = KeyPairManager.privateKeyPurposes,
            publicKeyPurposes = KeyPairManager.publicKeyPurposes,
            keyAccessGuard = KeyAccessGuard.Unguarded,
        ).getOrThrow() // TODO Use return value to store it in fileDir
    }

    override suspend fun retrievePublicKey(): ByteArray = Dispatchers.IO {
        val ks = KeyStore.getInstance(keyStoreProvider).also {
            it.load(null)
        }
        val aliases = ks.aliases()
        ks.getCertificate(aliases.nextElement()).publicKey.encoded
    }
}
