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
@file:OptIn(ExperimentalForeignApi::class)

package com.infomaniak.auth.lib.extensions

import com.infomaniak.auth.lib.internal.toNSError
import com.infomaniak.auth.lib.internal.utils.Xor
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFErrorRefVar
import platform.Foundation.NSError

/**
 * Helpful for C-style CoreFoundation functions that take a pointer of an error ref.
 *
 * Example usage:
 * ```
 * val result = tryIt { errorPointer -> SecKeyCopyExternalRepresentation(publicKeyRef, errorPointer) }
 *
 * when (result) {
 *      is Xor.First -> return result.value // Successful
 *      is Xor.Second -> {
 *          println("Error: ${result.value.localizedDescription}")
 *          handleNSError(result.value)
 *          return null
 *      }
 * }
 * ```
 */
internal inline fun <R : Any> tryIt(block: (errorPointer: CPointer<CFErrorRefVar>) -> R?): Xor<R, NSError> = memScoped {
    val errorVar = alloc<CFErrorRefVar>()
    val result = block(errorVar.ptr)
    when (val error = errorVar.value?.toNSError()) {
        null -> Xor.First(result!!)
        else -> Xor.Second(error)
    }
}
