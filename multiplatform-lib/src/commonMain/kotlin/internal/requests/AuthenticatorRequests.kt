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
package com.infomaniak.auth.lib.internal.requests

import com.infomaniak.auth.lib.internal.db.AccountsDao
import com.infomaniak.auth.lib.internal.models.SuccessfulApiResponse
import com.infomaniak.auth.lib.internal.network.ApiRoutes
import com.infomaniak.auth.lib.internal.network.utils.decode
import com.infomaniak.auth.lib.internal.utils.dynamicLazyMap
import com.infomaniak.auth.lib.models.migration.SharedApiToken
import com.infomaniak.auth.lib.models.migration.user.SharedUserProfile
import com.infomaniak.auth.lib.network.exceptions.ApiException
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job

internal class AuthenticatorRequests(
    private val createHttpClient: (userConfiguration: HttpClientConfig<*>.() -> Unit) -> HttpClient,
    private val getTokenForUser: suspend (userId: Long) -> SharedApiToken?,
    private val refreshToken: suspend (userId: Long) -> SharedApiToken,
    private val disconnectAccount: suspend (userId: Long) -> Unit,
    private val routes: ApiRoutes,
    private val accountsDao: AccountsDao,
    coroutineScope: CoroutineScope,
) {

    private val perUserHttpClient = coroutineScope.dynamicLazyMap(
        cacheManager = { userId: Long, _ ->
            accountsDao.getAccountAsFlow(userId).first { it == null }
        }
    ) { userId: Long ->
        val parentJob = this.coroutineContext.job
        async(Dispatchers.IO) {
            createHttpClient { configureHttpClientForUser(userId) }.also { httpClient ->
                parentJob.invokeOnCompletion {
                    httpClient.close()
                    httpClient.engine.close()
                }
            }
        }
    }

    suspend fun getUserProfile(
        userId: Long,
    ): SharedUserProfile {
        val url = "${routes.userProfile()}&with=security"

        return httpClientForUser(userId).get(url).decode<SuccessfulApiResponse<SharedUserProfile>>().data
    }

    private suspend fun httpClientForUser(userId: Long): HttpClient {
        return perUserHttpClient.useElement(userId) { it.await() }
    }

    private fun HttpClientConfig<*>.configureHttpClientForUser(userId: Long) {
        install(Auth) {
            bearer {
                sendWithoutRequest { true }
                refreshTokens { refreshTokenOrDisconnectAccount(userId) }
                loadTokens { getTokenForUser(userId)?.toBearerTokens() }
            }
        }
    }

    private suspend fun refreshTokenOrDisconnectAccount(userId: Long): BearerTokens? = try {
        refreshToken(userId).toBearerTokens()
    } catch (e: ApiException) {
        if (e.statusCode == 401 || e.isBrokenInvalidPasskeyResponse()) {
            disconnectAccount(userId)
            null
        } else throw e
    }

    private fun ApiException.isBrokenInvalidPasskeyResponse(): Boolean {
        //TODO[Authenticator-DONT-SHIP]: Remove this and its usage before public release.
        return this is ApiException.ApiErrorException && statusCode == 422 && errorCode == "invalid_passkey"
    }

    private fun SharedApiToken.toBearerTokens() = BearerTokens(
        accessToken = accessToken,
        refreshToken = null // Not needed, we're doing it with the passkey.
    )
}
