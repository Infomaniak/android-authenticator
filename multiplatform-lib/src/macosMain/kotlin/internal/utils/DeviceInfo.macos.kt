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

package com.infomaniak.auth.lib.internal.utils

import com.infomaniak.auth.lib.internal.webauthn.DeviceInfo
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.darwin.sysctlbyname
import platform.posix.size_tVar

internal actual fun getDeviceInfo(): DeviceInfo {
    return DeviceInfo(
        brand = "Apple",
        model = getHardwareModel(),
        platform = "macos",
    )
}

private fun getHardwareModel(): String = memScoped {
    val sizePtr = alloc<size_tVar>()
    sysctlbyname("hw.model", null, sizePtr.ptr, null, 0uL)
    val size = sizePtr.value

    if (size > 0uL) {
        val buffer = allocArray<ByteVar>(size.toInt())

        when (sysctlbyname("hw.model", buffer, sizePtr.ptr, null, 0uL)) {
            0 -> buffer.toKString()
            else -> "Unknown Mac"
        }
    } else {
        "Unknown Mac"
    }
}
