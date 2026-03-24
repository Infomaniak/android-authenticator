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
package com.infomaniak.auth.lib.internal.network.utils

import com.infomaniak.auth.lib.network.exceptions.UnknownException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.CancellationException
import network.exceptions.ApiException
import network.exceptions.NetworkException

internal const val CONTENT_REQUEST_ID_HEADER = "x-request-id"

internal fun HttpResponse.getRequestContextId() = headers[CONTENT_REQUEST_ID_HEADER] ?: ""

internal suspend inline fun <reified R> HttpResponse.decode(): R = runCatching {
    body<R>()
}.getOrElse { exception ->
    when (exception) {
        is CancellationException, is NetworkException, is ApiException -> throw exception
        else -> throw UnknownException(exception)
    }
}
