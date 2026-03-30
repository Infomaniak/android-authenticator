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

import com.infomaniak.auth.lib.internal.AuthenticatorFacadeImpl
import com.infomaniak.auth.lib.internal.db.getAccountsRoomDatabase
import com.infomaniak.auth.lib.internal.managers.AuthenticatorManager
import com.infomaniak.auth.lib.internal.managers.MigrationManager
import com.infomaniak.auth.lib.internal.network.ApiClientProvider
import com.infomaniak.auth.lib.internal.repositories.AccountsRepository
import com.infomaniak.auth.lib.internal.repositories.WebAuthnRepository
import com.infomaniak.auth.lib.internal.requests.AuthenticatorRequest
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import com.infomaniak.auth.lib.network.interfaces.TokenBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import network.utils.ApiEnvironment

abstract class AuthenticatorFacade internal constructor() {
    companion object {

        fun create(
            environment: ApiEnvironment,
            userAgent: String,
            clientId: String,
            databaseNameOrPath: String? = null,
            crashReport: CrashReportInterface,
            tokenBridge: TokenBridge,
            scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
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
            val accountsDatabase = getAccountsRoomDatabase(databaseNameOrPath)
            val accountsRepository = AccountsRepository(accountsDatabase)
            val authenticatorManager = AuthenticatorManager(
                webAuthnRepository = webAuthnRepository,
                accountsRepository = accountsRepository
            )
            val migrationManager = MigrationManager(
                accountsDatabase = accountsDatabase,
                authenticatorManager = authenticatorManager,
                webAuthnRepository = webAuthnRepository,
                clientId = clientId,
            )
            return AuthenticatorFacadeImpl(
                accountsDatabase = accountsDatabase,
                clientId = clientId,
                authenticatorManager = authenticatorManager,
                migrationManager = migrationManager,
                tokenBridge = tokenBridge,
                coroutineScope = scope,
            )
        }
    }

    abstract val accounts: Flow<List<Account>>

    abstract val appStatus: SharedFlow<AppStatus>

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

    /**
     * Refresh the token for the specific userId
     */
    @Throws(Exception::class)
    abstract suspend fun refreshTokenFor(userId: Long)
}
