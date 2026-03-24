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
import com.infomaniak.auth.lib.AuthenticatorInjection
import com.infomaniak.auth.manager.AccountUtils
import com.infomaniak.core.auth.room.UserDatabase
import com.infomaniak.core.common.utils.buildUserAgent
import com.infomaniak.lib.login.ApiToken
import com.infomaniak.lib.login.InfomaniakLogin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun providesInfomaniakLogin(@ApplicationContext appContext: Context): InfomaniakLogin {
        return InfomaniakLogin(
            context = appContext,
            loginUrl = "https://login.staging-authenticator.dev.infomaniak.ch/",
            appUID = BuildConfig.APPLICATION_ID,
            clientID = BuildConfig.CLIENT_ID,
            accessType = null,
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
        authenticatorInjection: AuthenticatorInjection,
        accountUtils: AccountUtils,
    ): AuthenticatorFacade {
        return authenticatorInjection.getAuthenticatorFacade(
            clientId = BuildConfig.CLIENT_ID,
            getTokenFromDatabase = { userId ->
                accountUtils.users.first().firstOrNull { userId == it.id.toLong() }?.apiToken?.accessToken
            },
            getTokenFromCrossAppLogin = { userId ->
                // TODO[Authenticator]: retrieve token from crossapplogin
                null
            },
            persistTokenForAccount = { userId, token ->
                val dao = UserDatabase().userDao()
                val user = dao.findById(userId.toInt()) ?: return@getAuthenticatorFacade
                dao.update(user.copy(apiToken = ApiToken(accessToken = token, tokenType = "Bearer", userId = userId.toInt())))
            },
        )
    }
}
