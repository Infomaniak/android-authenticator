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
package com.infomaniak.auth.lib.internal.network

import network.utils.ApiEnvironment

internal object ApiRoutes {

    fun apiBaseUrl(environment: ApiEnvironment) = "${environment.baseUrl}/api/"

    fun passkeysOptions() = "users/me/passkeys/options"
    fun registerPasskey() = "users/me/passkeys"
    fun delete(passkeyId: String) = "users/me/passkeys/$passkeyId"
    fun migrationsOptions() = "authenticator/migrations"
    fun verifyMigration(sessionId: String) = "authenticator/migrations/$sessionId/verify"
    fun finishMigration(sessionId: String) = "users/me/authenticator/migrations/$sessionId"
    fun challenge() = "authenticator/challenge"
    fun verify() = "authenticator/verify"
}
