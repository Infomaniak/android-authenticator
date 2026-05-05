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
@file:OptIn(ExperimentalContracts::class)

package com.infomaniak.auth.lib.internal.extensions

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFArrayGetCount
import platform.CoreFoundation.CFArrayGetValueAtIndex
import platform.CoreFoundation.CFArrayRef
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@ExperimentalForeignApi
@Suppress("unchecked_cast")
internal operator fun <T : CPointer<*>?> CFArrayRef.get(index: Long): T = CFArrayGetValueAtIndex(this, index) as T

@ExperimentalForeignApi
internal val CFArrayRef.size: Long inline get() = CFArrayGetCount(this)

@ExperimentalForeignApi
internal fun CFArrayRef?.isNullOrEmpty(): Boolean {
    contract { returns(false) implies (this@isNullOrEmpty != null) }
    return this == null || size == 0L
}
