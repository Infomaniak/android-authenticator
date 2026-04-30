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
package com.infomaniak.auth.lib.internal.network

import com.infomaniak.auth.lib.internal.models.ApiResponseForError
import com.infomaniak.auth.lib.internal.network.utils.getHttpClientEngine
import com.infomaniak.auth.lib.internal.network.utils.getRequestContextId
import com.infomaniak.auth.lib.network.exceptions.ApiException
import com.infomaniak.auth.lib.network.exceptions.NetworkException
import com.infomaniak.auth.lib.network.interfaces.BreadcrumbType
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import com.infomaniak.auth.lib.network.interfaces.CrashReportLevel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

internal class ApiClientProvider(
    private val userAgent: String,
    private val routes: ApiRoutes,
    private val crashReport: CrashReportInterface? = null,
) {

    private val jsonConfig = Json {
        /** From [io.ktor.serialization.kotlinx.json.DefaultJson] */
        encodeDefaults = true
        isLenient = true
        allowSpecialFloatingPointValues = true
        allowStructuredMapKeys = true
        prettyPrint = false
        useArrayPolymorphism = false

        // Use-case specific config:
        coerceInputValues = true // Use default values if not recognized (used for enums).
        ignoreUnknownKeys = true // Don't break if keys are added.
        @OptIn(ExperimentalSerializationApi::class)
        decodeEnumsCaseInsensitive = true
        useAlternativeNames = false
    }

    val httpClient = HttpClient(getHttpClientEngine()) {
        install(UserAgent) {
            agent = userAgent
        }
        install(ContentNegotiation) {
            json(jsonConfig)
        }
        install(ContentEncoding) {
            gzip()
        }
        install(HttpTimeout) {
            // Each value can be fine-tuned independently, hence the value not being shared.
            connectTimeoutMillis = 10.seconds.inWholeMilliseconds
            socketTimeoutMillis = 10.seconds.inWholeMilliseconds
        }
        install(HttpRequestRetry) {
            retryOnExceptionIf(maxRetries = MAX_RETRY) { _, cause ->
                cause.isNetworkException()
            }
            delayMillis { retry ->
                retry * 500L
            }
        }

        defaultRequest {
            url(routes.apiBaseUrl())
            contentType(ContentType.Application.Json)
        }

        HttpResponseValidator {
            validateResponse(::validateResponse)
            handleResponseExceptionWithRequest(::handleResponseExceptionWithRequest)
        }
    }

    private suspend fun validateResponse(response: HttpResponse) {
        val requestContextId = response.getRequestContextId()
        val statusCode = response.status.value

        addSentryUrlBreadcrumb(response, statusCode, requestContextId)

        if (statusCode >= 300) {
            val bodyResponse = response.bodyAsText()
            val apiError = runCatching {
                jsonConfig.decodeFromString<ApiResponseForError>(bodyResponse)
            }.getOrElse {
                throw ApiException.UnexpectedApiErrorFormatException(statusCode, bodyResponse, null, requestContextId)
            }
            throw ApiException.ApiErrorException(statusCode, apiError.error.code, apiError.error.description, requestContextId)
        }
    }

    private suspend fun handleResponseExceptionWithRequest(cause: Throwable, request: HttpRequest) {
        when (cause) {
            is IOException -> throw NetworkException("Network error: ${cause.message}", cause)
            is ApiException, is CancellationException -> throw cause
            else -> {
                val response = runCatching { request.call.response }.getOrNull()
                val requestContextId = response?.getRequestContextId() ?: ""
                val bodyResponse = response?.bodyAsText() ?: cause.message ?: ""
                val statusCode = response?.status?.value ?: -1
                throw ApiException.UnexpectedApiErrorFormatException(
                    statusCode,
                    bodyResponse,
                    cause,
                    requestContextId
                )
            }
        }
    }

    private fun addSentryUrlBreadcrumb(response: HttpResponse, statusCode: Int, requestContextId: String) {
        val requestUrl = response.request.url
        val data = buildMap {
            put("url", "${requestUrl.protocol.name}://${requestUrl.host}${requestUrl.encodedPath}")
            put("method", response.request.method.value)
            put("status_code", "$statusCode")
            if (requestUrl.encodedQuery.isNotEmpty()) put("http.query", requestUrl.encodedQuery)
            put("request_id", requestContextId)
            put("http.start_timestamp", "${response.requestTime.timestamp}")
            put("http.end_timestamp", "${response.responseTime.timestamp}")
            response.contentLength()?.let { put("response_content_length", "$it") }
        }
        crashReport?.addBreadcrumb(
            message = "",
            category = "http",
            level = CrashReportLevel.INFO,
            type = BreadcrumbType.HTTP,
            data = data
        )
    }

    private fun Throwable.isNetworkException() = this is IOException

    companion object {
        private const val MAX_RETRY = 3
    }
}
