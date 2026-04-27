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

package com.infomaniak.auth.lib.internal.extensions

import com.infomaniak.auth.lib.internal.utils.Xor
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
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
 *
 * See [tryIt2] for the full NSError variant (no C-style CoreFoundation).
 */
internal inline fun <R : Any> tryIt(block: (errorPointer: CPointer<CFErrorRefVar>) -> R?): Xor<R, NSError> = memScoped {
    val errorVar = alloc<CFErrorRefVar>()
    val result = block(errorVar.ptr)
    when (val error = errorVar.value?.toNSError()) {
        null -> Xor.First(result!!)
        else -> Xor.Second(error)
    }
}

/**
 * Helpful for functions that take a pointer of an error ref.
 *
 * Example usage:
 * ```
 * val result = tryIt2 { errorPointer ->
 *     NSFileManager.defaultManager.URLForDirectory(
 *         directory = NSApplicationSupportDirectory,
 *         inDomain = NSUserDomainMask,
 *         appropriateForURL = null,
 *         create = true,
 *         error = errorPointer,
 *     )
 * }
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
 *
 * See [tryIt] For the C-style CoreFoundation compatible variant.
 */
@OptIn(BetaInteropApi::class)
internal inline fun <R : Any> tryIt2(block: (errorPtr: CPointer<ObjCObjectVar<NSError?>>) -> R?): Xor<R, NSError> = memScoped {
    val errorVar = alloc<ObjCObjectVar<NSError?>>()
    val result = block(errorVar.ptr)
    when (val error = errorVar.value) {
        null -> Xor.First(result!!)
        else -> Xor.Second(error)
    }
}
