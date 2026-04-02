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
package com.infomaniak.auth.di

import android.content.Context
import com.infomaniak.auth.BuildConfig
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.lib.network.interfaces.BreadcrumbType
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import com.infomaniak.auth.lib.network.interfaces.CrashReportLevel
import com.infomaniak.auth.lib.network.interfaces.TokenBridge
import com.infomaniak.auth.utils.AccountUtils
import com.infomaniak.core.auth.room.UserDatabase
import com.infomaniak.core.common.utils.buildUserAgent
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade.AccountsCheckingStatus
import com.infomaniak.core.twofactorauth.back.TwoFactorAuthManager
import com.infomaniak.lib.login.ApiToken
import com.infomaniak.lib.login.InfomaniakLogin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import network.utils.ApiEnvironment
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    private val appScope = CoroutineScope(Dispatchers.Default)

    @Provides
    @Singleton
    fun providesInfomaniakLogin(@ApplicationContext appContext: Context): InfomaniakLogin {
        return InfomaniakLogin(
            context = appContext,
            loginUrl = "https://login.staging-authenticator.dev.infomaniak.ch/",
            appUID = BuildConfig.APPLICATION_ID,
            clientID = BuildConfig.CLIENT_ID,
            accessType = null,
            sentryCallback = { _, _ -> }
        )
    }

    @UserAgent
    @Provides
    @Singleton
    fun providesUserAgent(): String {
        return buildUserAgent(
            appId = BuildConfig.APPLICATION_ID,
            appVersionCode = BuildConfig.VERSION_CODE,
            appVersionName = BuildConfig.VERSION_NAME,
        )
    }

    @Provides
    @Singleton
    fun provideAuthenticatorFacade(
        @UserAgent userAgent: String,
        accountUtils: AccountUtils,
        crossAppLoginFacade: CrossAppLoginFacade,
    ): AuthenticatorFacade {
        return AuthenticatorFacade.create(
            environment = ApiEnvironment.Staging,
            userAgent = userAgent,
            clientId = BuildConfig.CLIENT_ID,
            crashReport = createCrashReportInterface(),
            tokenBridge = createTokenBridge(accountUtils, crossAppLoginFacade),
        )
    }

    @Provides
    @Singleton
    fun provideCrossAppLoginFacade(): CrossAppLoginFacade {
        return CrossAppLoginFacade.create(
            applicationId = BuildConfig.APPLICATION_ID,
            clientId = BuildConfig.CLIENT_ID,
            scope = appScope
        )
    }

    @Provides
    @Singleton
    fun provideTwoFactorAuthManager(accountUtils: AccountUtils) =
        TwoFactorAuthManager { userId -> accountUtils.getHttpClient(userId) }

    private fun createCrashReportInterface() = object : CrashReportInterface {
        override fun addBreadcrumb(
            message: String,
            category: String,
            level: CrashReportLevel,
            type: BreadcrumbType,
            data: Map<String, String>?
        ) {
            //TODO[Authenticator] forward to sentry
        }

        override fun capture(
            message: String,
            error: Throwable,
            data: Map<String, String>?
        ) {
            //TODO[Authenticator] forward to sentry
        }

        override fun capture(
            message: String,
            data: Map<String, String>?,
            level: CrashReportLevel?
        ) {
            //TODO[Authenticator] forward to sentry
        }
    }

    private fun createTokenBridge(accountUtils: AccountUtils, crossAppLoginFacade: CrossAppLoginFacade) = object : TokenBridge {

        override suspend fun getTokenFromCrossAppLogin(
            userId: Long
        ): String? = crossAppLoginFacade.accountsCheckingState.transform { state ->
            val matchingAccount = state.checkedAccounts.find { it.id == userId }
                ?: if (state.status is AccountsCheckingStatus.UpToDate) null else return@transform
            emit(matchingAccount?.tokens?.first())

        }.first()

        override suspend fun getTokenFromDatabase(userId: Long): String? {
            return accountUtils.getUserById(userId.toInt())?.apiToken?.accessToken
        }

        override suspend fun persistTokenForAccount(userId: Long, token: String) {
            val dao = UserDatabase.getDatabase().userDao()
            val user = accountUtils.getUserById(userId.toInt()) ?: return
            dao.update(
                user.copy(
                    apiToken = ApiToken(
                        accessToken = token,
                        tokenType = user.apiToken.tokenType,
                        userId = userId.toInt()
                    )
                )
            )
        }
    }
}
