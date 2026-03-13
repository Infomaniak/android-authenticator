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
package network.utils

internal object ApiRoutes {

    fun apiBaseUrl(environment: ApiEnvironment) = "${environment.baseUrl}/api/"

    const val getPasskeysOptions = "users/me/passkeys/options"
    const val registerPasskey = "users/me/passkeys"
    const val challenge = "authenticator/challenge"
    const val verify = "authenticator/verify"
}
