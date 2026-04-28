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

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDateRef
import platform.CoreFoundation.CFErrorRef
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSError

// The casts below are fine because they involve "toll-free bridged" types.
// See Apple doc archive on it:
// https://developer.apple.com/library/archive/documentation/CoreFoundation/Conceptual/CFDesignConcepts/Articles/tollFreeBridgedTypes.html#//apple_ref/doc/uid/TP40010677

@Suppress("unchecked_cast") // It works. Source: trust us.
internal fun NSData.toCFDataRef() = CFBridgingRetain(this) as CFDataRef

internal fun CFDataRef.toNSData(): NSData = CFBridgingRelease(this) as NSData

internal fun CFDateRef.toNSDate(): NSDate = CFBridgingRelease(this) as NSDate

internal fun CFErrorRef.toNSError(): NSError = CFBridgingRelease(this) as NSError
