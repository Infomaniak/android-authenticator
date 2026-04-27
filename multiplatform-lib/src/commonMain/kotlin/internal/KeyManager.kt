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

internal interface KeyPairManager {

    /**
     * Generates key pair for a new registration
     * (migrating from kAuth v1 or a backup, or a fresh new login)
     */
    suspend fun generateNewKey(userId: Long, keyId: String): Failure.KeyManagement.GenerationFailed?

    suspend fun retrievePublicKey(userId: Long, keyId: String): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed>

    suspend fun retrievePrivateKey(userId: Long, keyId: String): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed>

    suspend fun findKeyIdFor(predicate: (name: String) -> Boolean): String?

    suspend fun deleteKeysMatching(predicate: (name: String) -> Boolean): Xor<Unit, Failure.KeyManagement.KeyNotFound>

    companion object {
        val privateKeyPurposes = KeyPurposes.privateKeyDefaults
        val publicKeyPurposes = KeyPurposes.publicKeyDefaults
    }
}
