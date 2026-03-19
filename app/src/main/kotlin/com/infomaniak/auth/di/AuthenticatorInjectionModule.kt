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

import com.infomaniak.auth.lib.AuthenticatorInjection
import com.infomaniak.auth.lib.network.interfaces.BreadcrumbType
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import com.infomaniak.auth.lib.network.interfaces.CrashReportLevel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import network.utils.ApiEnvironment
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticatorInjectionModule {

    @Provides
    @Singleton
    fun provideAuthenticatorInjection(
        @UserAgent userAgent: String,
    ): AuthenticatorInjection {
        return AuthenticatorInjection(
            environment = ApiEnvironment.Staging,
            userAgent = userAgent,
            crashReport = object : CrashReportInterface {
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
            },
        )
    }
}
