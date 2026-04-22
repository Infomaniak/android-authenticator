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
import com.infomaniak.auth.lib.models.migration.user.UserProfile
import com.infomaniak.auth.lib.network.interfaces.BreadcrumbType
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import com.infomaniak.auth.lib.network.interfaces.CrashReportLevel
import com.infomaniak.auth.lib.network.interfaces.TokenBridge
import com.infomaniak.auth.lib.network.interfaces.UserProfileBridge
import com.infomaniak.auth.utils.AccountUtils
import com.infomaniak.auth.utils.toUser
import com.infomaniak.core.auth.room.UserDatabase
import com.infomaniak.core.common.utils.buildUserAgent
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade.AccountsCheckingStatus
import com.infomaniak.core.network.ApiEnvironment
import com.infomaniak.core.network.LOGIN_ENDPOINT_URL
import com.infomaniak.core.twofactorauth.back.TwoFactorAuthManager
import com.infomaniak.lib.login.ApiToken
import com.infomaniak.lib.login.InfomaniakLogin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.Message
import io.sentry.protocol.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
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
            loginUrl = "${LOGIN_ENDPOINT_URL}/",
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
            apiHost = ApiEnvironment.current.host,
            userAgent = userAgent,
            clientId = BuildConfig.CLIENT_ID,
            crashReport = createCrashReportInterface(),
            tokenBridge = createTokenBridge(accountUtils, crossAppLoginFacade),
            userProfileBridge = createUserProfileBridge(accountUtils),
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
            val breadcrumb = Breadcrumb()
            breadcrumb.message = message
            breadcrumb.category = category
            breadcrumb.level = level.sentryLevel
            breadcrumb.type = type.value
            data?.forEach { (key, value) -> breadcrumb.setData(key, value) }
            Sentry.addBreadcrumb(breadcrumb)
        }

        override fun capture(
            userId: Long,
            message: String,
            error: Throwable,
            data: Map<String, String>?
        ) {
            val sentryEvent = SentryEvent(error).apply {
                this.user = User().also { it.id = userId.toString() }
                data?.forEach { (key, value) -> setExtra(key, value) }
                this.message = Message().apply { this.message = message }
            }
            Sentry.captureEvent(sentryEvent)
        }

        override fun capture(
            userId: Long,
            message: String,
            data: Map<String, String>?,
            level: CrashReportLevel?
        ) {
            Sentry.captureMessage(message, level?.sentryLevel ?: SentryLevel.INFO) { scope ->
                scope.user = User().also { it.id = userId.toString() }
                data?.forEach { (key, value) -> scope.setExtra(key, value) }
            }
        }

        private val CrashReportLevel.sentryLevel: SentryLevel
            get() = when (this) {
                CrashReportLevel.DEBUG -> SentryLevel.DEBUG
                CrashReportLevel.INFO -> SentryLevel.INFO
                CrashReportLevel.WARNING -> SentryLevel.WARNING
                CrashReportLevel.ERROR -> SentryLevel.ERROR
                CrashReportLevel.FATAL -> SentryLevel.FATAL
            }
    }

    private fun createTokenBridge(accountUtils: AccountUtils, crossAppLoginFacade: CrossAppLoginFacade) = object : TokenBridge {

        override suspend fun getTokenFromCrossAppLogin(
            userId: Long
        ): String? = crossAppLoginFacade.accountsCheckingState.transform { state ->
            val matchingAccount = state.checkedAccounts.find { it.id == userId }
                ?: when (state.status) {
                    AccountsCheckingStatus.Checking -> return@transform // Wait for next emission.
                    AccountsCheckingStatus.UpToDate -> null // Not found.
                    AccountsCheckingStatus.Error.Network -> null // TODO[Authenticator]: Consider auto-retrying on network change.
                    AccountsCheckingStatus.Error.Unknown -> null // Give up.
                }
            if (matchingAccount == null) return@transform emit(null)
            val result = crossAppLoginFacade.attemptLogin(listOf(matchingAccount))
            emit(result.tokens.singleOrNull()?.accessToken)

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

    private fun createUserProfileBridge(accountUtils: AccountUtils): UserProfileBridge = object : UserProfileBridge {
        override suspend fun persistUserProfile(userProfile: UserProfile) {
            val user = userProfile.toUser()
            accountUtils.addUser(user)
        }
    }
}
