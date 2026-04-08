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

import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface

internal suspend fun <R> withTimeoutOrNullAndReport(
    waitForTimeout: suspend () -> String,
    crashReportInterface: CrashReportInterface,
    block: suspend () -> R
): R? {
    val result: TimeoutResult<R> = raceOf(
        { TimeoutResult.Returned(block()) },
        { TimeoutResult.TimedOut(waitForTimeout()) }
    )
    return when (result) {
        is TimeoutResult.Returned -> result.value
        is TimeoutResult.TimedOut -> {
            crashReportInterface.capture(result.message)
            null
        }
    }
}

private sealed interface TimeoutResult<out T> {
    data class TimedOut(val message: String) : TimeoutResult<Nothing>
    data class Returned<T>(val value: T) : TimeoutResult<T>
}
