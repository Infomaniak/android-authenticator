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
package com.infomaniak.auth.lib.logging

import com.infomaniak.auth.lib.network.interfaces.BreadcrumbType
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import com.infomaniak.auth.lib.network.interfaces.CrashReportLevel
import kotlin.coroutines.cancellation.CancellationException

fun BlockLogger.Companion.breadcrumbsLogger(
    crashReportInterface: CrashReportInterface,
    category: String,
): BlockLogger<String> {
    val callbacks = object : BlockLogger.Callbacks<String>() {

        override fun blockEntered(blockIdentity: String, invocationId: Long) {
            crashReportInterface.addBreadcrumb(
                message = "↘ $blockIdentity#$invocationId entered",
                category = category,
                level = CrashReportLevel.INFO,
                type = BreadcrumbType.Default,
                data = null
            )
        }

        override fun blockReturned(blockIdentity: String, invocationId: Long, value: Any?) {
            crashReportInterface.addBreadcrumb(
                message = "↖ $blockIdentity#$invocationId returned",
                category = category,
                level = CrashReportLevel.INFO,
                type = BreadcrumbType.Default,
                data = buildMap {
                    runCatching { this["value"] = value.toString() }.onFailure {
                        this["value.toString() failure"] = it.toString()
                    }
                    if (value != null) this["value::class"] = value::class.qualifiedName ?: "null"
                }
            )
        }

        override fun blockReturnedEarly(blockIdentity: String, invocationId: Long) {
            crashReportInterface.addBreadcrumb(
                message = "↖ $blockIdentity#$invocationId returned early",
                category = category,
                level = CrashReportLevel.INFO,
                type = BreadcrumbType.Default,
                data = null
            )
        }

        override fun blockThrew(blockIdentity: String, invocationId: Long, throwable: Throwable) {
            val isCancellation = throwable is CancellationException
            crashReportInterface.addBreadcrumb(
                message = "↖ $blockIdentity#$invocationId ${if (isCancellation) "got cancelled" else "threw" }",
                category = category,
                level = CrashReportLevel.INFO,
                type = BreadcrumbType.Default,
                data = buildMap {
                    this["exception message"] = throwable.message ?: "null"
                    this["exception type"] = throwable::class.qualifiedName ?: "unknown"
                    //TODO: Add causes recursively
                }
            )
        }
    }
    return BlockLogger(callbacks)
}
