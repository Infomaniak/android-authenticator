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
package com.infomaniak.auth.lib.internal.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Creates a Flow with the passed [block] using the values from this [DynamicLazyMap] corresponding to the passed [keys],
 * while it is hot.
 */
internal fun <K, E, R> DynamicLazyMap<K, E>.buildFlowWithElements(
    keys: Set<K>,
    block: (map: Map<K, E>) -> Flow<R>
): Flow<R> = flow {
    useElements(keys) {
        emitAll(block(it))
    }
}
