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

sealed class ApiEnvironment(val baseUrl: String) {
    data object Staging :
        ApiEnvironment("https://login.staging-authenticator.dev.infomaniak.ch")

    // Those urls are duplicated with the ones we have in Android so don't forget to change them also in Android
    data object Preprod :
        ApiEnvironment("https://authenticator.preprod.dev.infomaniak.ch") //TODO Change this to the final baseUrl

    data object Prod : ApiEnvironment("")
    data class Custom(private val url: String) : ApiEnvironment(url)
}
