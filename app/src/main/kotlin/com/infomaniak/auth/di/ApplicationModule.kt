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
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import androidx.room.withTransaction
import com.infomaniak.auth.BuildConfig
import com.infomaniak.auth.MainApplication
import com.infomaniak.auth.utils.AccountUtils
import com.infomaniak.auth.utils.toLoginApiToken
import com.infomaniak.auth.utils.toSharedApiToken
import com.infomaniak.auth.utils.toUser
import com.infomaniak.core.auth.models.TokenDeviceBinding
import com.infomaniak.core.auth.room.UserDatabase
import com.infomaniak.core.common.getAndroidId
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade.AccountsCheckingStatus
import com.infomaniak.core.login.InfomaniakLogin
import com.infomaniak.core.network.ApiEnvironment
import com.infomaniak.core.network.LOGIN_ENDPOINT_URL
import com.infomaniak.core.network.networking.HttpUtils
import com.infomaniak.core.twofactorauth.back.TwoFactorAuthManager
import com.infomaniak.multiplatform_authenticator.core.AuthenticatorFacade
import com.infomaniak.multiplatform_authenticator.core.models.migration.SharedApiToken
import com.infomaniak.multiplatform_authenticator.core.models.migration.user.SharedUserProfile
import com.infomaniak.multiplatform_authenticator.core.network.interfaces.AuthenticatorBridge
import com.infomaniak.multiplatform_authenticator.core.network.interfaces.BreadcrumbType
import com.infomaniak.multiplatform_authenticator.core.network.interfaces.CrashReportInterface
import com.infomaniak.multiplatform_authenticator.core.network.interfaces.CrashReportLevel
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

    @Provides
    @Singleton
    fun provideAuthenticatorFacade(
        accountUtils: AccountUtils,
        crossAppLoginFacade: CrossAppLoginFacade,
    ): AuthenticatorFacade {
        return AuthenticatorFacade.create(
            apiHost = ApiEnvironment.current.host,
            userAgent = HttpUtils.getUserAgent,
            clientId = BuildConfig.CLIENT_ID,
            crashReport = createCrashReportInterface(),
            authenticatorBridge = createAuthenticatorBridge(accountUtils, crossAppLoginFacade),
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

    private fun createAuthenticatorBridge(accountUtils: AccountUtils, crossAppLoginFacade: CrossAppLoginFacade) =
        object : AuthenticatorBridge {

            override suspend fun getTokenFromCrossAppLogin(
                userId: Long
            ): SharedApiToken? = crossAppLoginFacade.accountsCheckingState.transform { state ->
                val matchingAccount = state.checkedAccounts.find { it.id == userId }
                    ?: when (state.status) {
                        AccountsCheckingStatus.Checking -> return@transform // Wait for next emission.
                        AccountsCheckingStatus.UpToDate -> null // Not found.
                        AccountsCheckingStatus.Error.Network -> null // TODO[Authenticator]: Consider auto-retrying on network change.
                        AccountsCheckingStatus.Error.Unknown -> null // Give up.
                    }
                if (matchingAccount == null) return@transform emit(null)
                val result = crossAppLoginFacade.attemptLogin(listOf(matchingAccount))
                emit(result.tokens.singleOrNull()?.toSharedApiToken())
            }.first()

            override suspend fun getTokenFromDatabase(userId: Long): SharedApiToken? {
                return accountUtils.getUserById(userId.toInt())?.apiToken?.toSharedApiToken()
            }

            override suspend fun attemptPersistingTokenForAccount(userId: Long, token: SharedApiToken) {
                MainApplication.userDataCleanableList.forEach { it.resetForUser(userId) }
                val db = UserDatabase.getDatabase()
                val user = accountUtils.getUserById(userId.toInt()) ?: error("User not found for ID: $userId")
                db.useWriterConnection {
                    it.immediateTransaction {
                        val dao = db.userDao()
                        dao.update(user.copy(apiToken = token.toLoginApiToken()))
                        dao.upsertTokenDeviceBinding(TokenDeviceBinding(userId.toInt(), getAndroidId()))
                    }
                }
            }

            override suspend fun persistUserProfile(userProfile: SharedUserProfile) {
                val db = UserDatabase.getDatabase()
                if (userProfile.apiToken.accessToken.isNotEmpty()) {
                    MainApplication.userDataCleanableList.forEach { it.resetForUser(userProfile.id.toLong()) }
                    db.userDao().upsert(userProfile.toUser())
                } else {
                    db.withTransaction {
                        userProfile.apiToken = db.userDao().findById(userProfile.id)?.apiToken?.toSharedApiToken()
                            ?: return@withTransaction
                        db.userDao().update(userProfile.toUser())
                    }
                }
            }
        }
}
