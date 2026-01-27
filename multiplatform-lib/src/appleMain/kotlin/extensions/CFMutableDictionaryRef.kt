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
package com.infomaniak.auth.lib.extensions

import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.ptr
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryGetCount
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFIndex
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFNumberCreate
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanFalse
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFNumberIntType
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData

@ExperimentalForeignApi
internal fun buildCFDictionary(
    capacity: CFIndex = 0L,
    builderAction: CFMutableDictionaryRef?.() -> Unit
): CFDictionaryRef? {
    return CFDictionaryCreateMutable(
        allocator = kCFAllocatorDefault,
        capacity = capacity,
        keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
        valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr
    ).apply(builderAction)
}


@ExperimentalForeignApi
internal operator fun CFMutableDictionaryRef?.set(key: CValuesRef<*>?, value: CValuesRef<*>?) {
    CFDictionaryAddValue(this, key, value)
}

@ExperimentalForeignApi
internal operator fun CFMutableDictionaryRef?.set(key: CValuesRef<*>?, value: Boolean) {
    this[key] = if (value) kCFBooleanTrue else kCFBooleanFalse
}

@ExperimentalForeignApi
internal operator fun CFMutableDictionaryRef?.set(key: CValuesRef<*>?, value: Int) {
    this[key] = CFNumberCreate(kCFAllocatorDefault, kCFNumberIntType, cValuesOf(value))
    // this[key] = CFNumberCreate(kCFAllocatorDefault, kCFNumberIntType, CFBridgingRetain(value))
}

@ExperimentalForeignApi
internal operator fun CFMutableDictionaryRef?.set(key: CValuesRef<*>?, value: NSData?) {
    this[key] = CFBridgingRetain(value) as CFDataRef
}

@ExperimentalForeignApi
internal val CFDictionaryRef?.size: Long inline get() = CFDictionaryGetCount(this)
