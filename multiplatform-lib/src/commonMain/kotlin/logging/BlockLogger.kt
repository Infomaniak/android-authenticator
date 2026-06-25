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
@file:OptIn(ExperimentalAtomicApi::class)

package com.infomaniak.auth.lib.logging

import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

class BlockLogger<BlockIdentity>(
    @PublishedApi internal val callbacks: Callbacks<BlockIdentity>
) {
    companion object {
        @PublishedApi
        internal val logId = AtomicLong(0)
    }

    inline fun <R> withLog(
        blockIdentity: BlockIdentity,
        block: () -> R
    ): R {
        val invocationId = logId.incrementAndFetch()
        var returnedOrThrew = false
        try {
            callbacks.blockEntered(blockIdentity, invocationId)
            return block().also {
                returnedOrThrew = true
                callbacks.blockReturned(blockIdentity, invocationId, it)
            }
        } catch (t: Throwable) {
            returnedOrThrew = true
            callbacks.blockThrew(blockIdentity, invocationId, t)
            throw t
        } finally {
            if (!returnedOrThrew) callbacks.blockReturnedEarly(blockIdentity, invocationId)
        }
    }

    abstract class Callbacks<BlockIdentity> {
        abstract fun blockEntered(blockIdentity: BlockIdentity, invocationId: Long)
        abstract fun blockReturned(blockIdentity: BlockIdentity, invocationId: Long, value: Any?)
        abstract fun blockReturnedEarly(blockIdentity: BlockIdentity, invocationId: Long)
        abstract fun blockThrew(blockIdentity: BlockIdentity, invocationId: Long, throwable: Throwable)
    }
}
