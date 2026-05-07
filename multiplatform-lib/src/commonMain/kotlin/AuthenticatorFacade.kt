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
import com.infomaniak.auth.lib.internal.network.ApiRoutes
import com.infomaniak.auth.lib.internal.repositories.AccountsRepository
import com.infomaniak.auth.lib.internal.requests.WebAuthnRequests
import com.infomaniak.auth.lib.models.migration.user.SharedUserProfile
import com.infomaniak.auth.lib.network.interfaces.AuthenticatorBridge
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

abstract class AuthenticatorFacade internal constructor() {

    abstract val accounts: Flow<List<Account>>

    abstract val appStatus: SharedFlow<AppStatus>

    inline fun <reified T : AppStatus> appStatusOrNull(): T? = appStatus.replayCache.first() as? T

    /**
     * Add successfully connected accounts.
     *
     * Will lead to [appStatus] to switch to the [AppStatus.LoggingIn] case.
     */
    abstract suspend fun addAccounts(connectedAccounts: List<SharedUserProfile>)

    /**
     * Remove account from the authenticator.
     */
    @Throws(Exception::class)
    abstract suspend fun removeAccount(token: String, id: Long)

    /**
     * Refresh the token for the specific userId
     */
    @Throws(Exception::class)
    abstract suspend fun refreshTokenFor(userId: Long)

    abstract fun refreshUserProfiles()

    companion object {

        fun create(
            apiHost: String,
            userAgent: String,
            clientId: String,
            databaseNameOrPath: String? = null,
            crashReport: CrashReportInterface,
            authenticatorBridge: AuthenticatorBridge,
            scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
        ): AuthenticatorFacade {
            val routes = ApiRoutes(apiHost)
            val webAuthnRequests = WebAuthnRequests(
                httpClient = ApiClientProvider(
                    scope = scope,
                    userAgent = userAgent,
                    routes = routes,
                    crashReport = crashReport,
                ).httpClient,
                routes = routes,
            )
            val accountsDatabase = getAccountsRoomDatabase(databaseNameOrPath)
            val accountsRepository = AccountsRepository(accountsDatabase)
            val authenticatorManager = AuthenticatorManager(
                webAuthnRequests = webAuthnRequests,
                accountsRepository = accountsRepository
            ).also { it.keyPairManager.ensureKeyPairsAreMoved() }
            val migrationManager = MigrationManager(
                accountsDatabase = accountsDatabase,
                authenticatorManager = authenticatorManager,
                webAuthnRequests = webAuthnRequests,
                clientId = clientId,
            )
            return AuthenticatorFacadeImpl(
                accountsDatabase = accountsDatabase,
                clientId = clientId,
                authenticatorManager = authenticatorManager,
                migrationManager = migrationManager,
                authenticatorBridge = authenticatorBridge,
                crashReport = crashReport,
                coroutineScope = scope,
            )
        }

    }
}
