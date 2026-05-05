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

import com.infomaniak.auth.lib.internal.Failure.KeyManagement.GenerationFailed
import com.infomaniak.auth.lib.internal.Failure.KeyManagement.KeyExtractionFailed
import com.infomaniak.auth.lib.internal.Failure.KeyManagement.KeyNotFound
import com.infomaniak.auth.lib.internal.utils.Xor

internal expect fun createKeyPairManager(): KeyPairManager

internal abstract class KeyPairManager protected constructor() {

    companion object {
        operator fun invoke(): KeyPairManager = createKeyPairManager()
    }

    open fun ensureKeyPairsAreMoved() = Unit

    /**
     * Generates key pair for a new registration
     * (migrating from kAuth v1 or a backup, or a fresh new login)
     */
    abstract suspend fun generateNewKey(userId: Long, keyId: String): GenerationFailed?

    abstract suspend fun retrievePublicKey(userId: Long, keyId: String): Xor<ByteArray, KeyExtractionFailed>

    abstract suspend fun retrievePrivateKey(userId: Long, keyId: String): Xor<ByteArray, KeyExtractionFailed>

    /** Sorted by creation date. */
    abstract suspend fun getSortedKeyIds(matchOn: MatchOn): List<String>

    abstract suspend fun findKeyIdFor(matchOn: MatchOn): String?

    abstract suspend fun deleteKeysMatching(matchOn: MatchOn): Xor<Unit, KeyNotFound>

    sealed interface MatchOn {
        class UserId(val id: Long) : MatchOn
        class PasskeyId(val id: String) : MatchOn
    }

    //region MatchOn to predicates
    protected fun MatchOn.asFilterPredicate(): (name: String) -> Boolean = when (this) {
        is MatchOn.PasskeyId -> asFilterPredicate()
        is MatchOn.UserId -> asFilterPredicate()
    }

    private fun MatchOn.UserId.asFilterPredicate() = { name: String ->
        name.startsWith("$id-") // Same on both platforms.
    }

    protected abstract fun MatchOn.PasskeyId.asFilterPredicate(): (name: String) -> Boolean // Platform dependent.
    //endregion
}
