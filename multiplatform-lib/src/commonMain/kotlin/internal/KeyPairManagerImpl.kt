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

internal expect class KeyPairManagerImpl() : KeyPairManager {
    override suspend fun generateNewKey(userId: Long, keyId: String): Failure.KeyManagement.GenerationFailed?
    override suspend fun retrievePublicKey(userId: Long, keyId: String): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed>
    override suspend fun retrievePrivateKey(
        userId: Long,
        keyId: String
    ): Xor<ByteArray, Failure.KeyManagement.KeyExtractionFailed>

    override suspend fun findKeyIdFor(userId: Long): Xor<String, Failure.KeyManagement.KeyNotFound>
    override suspend fun deleteKeysWith(name: String): Xor<Unit, Failure.KeyManagement.KeyNotFound>
}
