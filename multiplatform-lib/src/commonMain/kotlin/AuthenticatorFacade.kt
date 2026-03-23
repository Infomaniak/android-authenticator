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
package com.infomaniak.auth.lib

import com.infomaniak.auth.lib.db.getAccountsRoomDatabase
import com.infomaniak.auth.lib.managers.AuthenticatorManager
import com.infomaniak.auth.lib.network.ApiClientProvider
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import com.infomaniak.auth.lib.network.interfaces.TokenBridge
import com.infomaniak.auth.lib.network.repositories.WebAuthnRepository
import com.infomaniak.auth.lib.network.requests.AuthenticatorRequest
import com.infomaniak.auth.lib.repository.AccountsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import network.utils.ApiEnvironment
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

abstract class AuthenticatorFacade internal constructor() {
    companion object {

        fun create(
            environment: ApiEnvironment,
            userAgent: String,
            clientId: String,
            databaseNameOrPath: String? = null,
            crashReport: CrashReportInterface,
            tokenBridge: TokenBridge,
            scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
        ): AuthenticatorFacade {
            val webAuthnRepository = WebAuthnRepository(
                authenticatorRequest = AuthenticatorRequest(
                    httpClient = ApiClientProvider(
                        userAgent = userAgent,
                        environment = environment,
                        crashReport = crashReport,
                    ).httpClient
                )
            )
            val db = getAccountsRoomDatabase(databaseNameOrPath)
            val accountsRepository = AccountsRepository(db)
            val authenticatorManager = AuthenticatorManager(
                webAuthnRepository = webAuthnRepository,
                accountsRepository = accountsRepository
            )
            return AuthenticatorFacadeImpl(
                db = db,
                clientId = clientId,
                authenticatorManager = authenticatorManager,
                webAuthnRepository = webAuthnRepository,
                tokenBridge = tokenBridge,
                coroutineScope = scope,
            )
        }

        fun dummyInstance(
            userAgent: String,
            environment: ApiEnvironment,
            crashReport: CrashReportInterface?,
            scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
            loadingDurationMillis: Long = 2.seconds.inWholeMilliseconds,
            resetAfterMillis: Long = 20.seconds.inWholeMilliseconds,
        ): AuthenticatorFacade {
            val webAuthnRepository = WebAuthnRepository(
                authenticatorRequest = AuthenticatorRequest(
                    httpClient = ApiClientProvider(
                        userAgent = userAgent,
                        environment = environment,
                        crashReport = crashReport,
                    ).httpClient
                )
            )
            val accountsRepository = AccountsRepository(getAccountsRoomDatabase(databaseNameOrPath = null))
            val authenticatorManager =
                AuthenticatorManager(webAuthnRepository = webAuthnRepository, accountsRepository = accountsRepository)
            return DummyAuthenticatorFacade(
                accountsRepository = accountsRepository,
                authenticatorManager = authenticatorManager,
                scope = scope,
                loadingDuration = loadingDurationMillis.milliseconds,
                resetAfter = resetAfterMillis.milliseconds,
            )
        }
    }

    abstract val accounts: Flow<List<Account>>

    abstract val appStatus: Flow<AppStatus>

    /**
     * Add successfully connected accounts.
     *
     * Will lead to [appStatus] to switch to the [AppStatus.LoggingIn] case.
     */
    abstract suspend fun addAccounts(connectedAccounts: List<Account>)

    /**
     * Remove account from the authenticator.
     */
    abstract suspend fun removeAccount(token: String, id: Long)
}
