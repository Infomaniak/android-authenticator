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
package com.infomaniak.auth.lib.network

import com.infomaniak.auth.lib.network.interfaces.BreadcrumbType
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import com.infomaniak.auth.lib.network.interfaces.CrashReportLevel
import com.infomaniak.auth.lib.network.models.ApiError
import com.infomaniak.auth.lib.network.utils.getRequestContextId
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
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
import network.exceptions.ApiException
import network.exceptions.ApiException.ApiErrorException
import network.exceptions.ApiException.UnexpectedApiErrorFormatException
import network.exceptions.NetworkException
import network.utils.ApiEnvironment
import network.utils.ApiRoutes
import kotlin.time.Duration.Companion.seconds

internal class ApiClientProvider(
    private val userAgent: String,
    private val environment: ApiEnvironment,
    private val crashReport: CrashReportInterface? = null,
) {

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        useAlternativeNames = false
    }

    val httpClient = createHttpClient()

    fun createHttpClient(): HttpClient {
        val block: HttpClientConfig<*>.() -> Unit = {
            install(UserAgent) {
                agent = userAgent
            }
            install(ContentNegotiation) {
                val jsonConfig = Json {
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
                }
                json(jsonConfig)
            }
            install(ContentEncoding) {
                gzip()
            }
            install(HttpTimeout) {
                // Each value can be fine-tuned independently, hence the value not being shared.
                requestTimeoutMillis = 10.seconds.inWholeMilliseconds
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
                url(ApiRoutes.apiBaseUrl(environment))
                contentType(ContentType.Application.Json)
            }

            HttpResponseValidator {
                validateResponse { response: HttpResponse ->
                    val requestContextId = response.getRequestContextId()
                    val statusCode = response.status.value

                    addSentryUrlBreadcrumb(response, statusCode, requestContextId)

                    if (statusCode >= 300) {
                        val bodyResponse = response.bodyAsText()
                        val apiError = runCatching {
                            json.decodeFromString<ApiError>(bodyResponse)
                        }.getOrElse {
                            throw UnexpectedApiErrorFormatException(statusCode, bodyResponse, null, requestContextId)
                        }
                        throw ApiErrorException(apiError.errorCode, apiError.message, requestContextId)
                    }
                }
                handleResponseExceptionWithRequest { cause, request ->
                    when (cause) {
                        is IOException -> throw NetworkException("Network error: ${cause.message}")
                        is ApiException, is CancellationException -> throw cause
                        else -> {
                            val response = runCatching { request.call.response }.getOrNull()
                            val requestContextId = response?.getRequestContextId() ?: ""
                            val bodyResponse = response?.bodyAsText() ?: cause.message ?: ""
                            val statusCode = response?.status?.value ?: -1
                            throw UnexpectedApiErrorFormatException(
                                statusCode,
                                bodyResponse,
                                cause,
                                requestContextId
                            )
                        }
                    }
                }
            }
        }

        return HttpClient(block)
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
