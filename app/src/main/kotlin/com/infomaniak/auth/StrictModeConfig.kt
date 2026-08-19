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
package com.infomaniak.auth

import android.os.Build.VERSION.SDK_INT
import android.os.StrictMode
import android.os.strictmode.Violation
import androidx.annotation.RequiresApi
import io.sentry.Sentry
import io.sentry.SentryLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

private val classNamePrefixesToIgnore = listOf(
    "okhttp3.",
    "org.matomo.sdk.",
)

fun setupStrictMode() {
    if (BuildConfig.DEBUG) setupDebugStrictMode() else setupProductionThreadMonitoring()
}

private fun Violation.shouldBeReported(): Boolean {
    val shouldBeIgnored = stackTrace.any { element ->
        classNamePrefixesToIgnore.any { prefix -> element.className.startsWith(prefix) }
    }
    return !shouldBeIgnored
}

private fun setupDebugStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyFlashScreen()
            .let {
                if (SDK_INT >= 28) it.penaltyListener(Dispatchers.Default.asExecutor()) { violation ->
                    if (violation.shouldBeReported()) violation.printStackTrace()
                } else it.penaltyLog()
            }
            .build()
    )
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
    )
}

private fun setupProductionThreadMonitoring() {
    if (SDK_INT >= 28) setupProductionThreadMonitoringApi28()
}

@RequiresApi(28)
private fun setupProductionThreadMonitoringApi28() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .detectCustomSlowCalls()
            .penaltyListener(Dispatchers.Default.asExecutor()) { violation ->
                if (violation.shouldBeReported()) Sentry.captureException(violation) {
                    it.level = SentryLevel.DEBUG
                }
            }
            .build()
    )
}
