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
package com.infomaniak.auth.lib.models

object UrlConstants {
    fun managerUrl(host: String, path: String) = "https://manager.$host/v3/$path"
    fun autologUrl(host: String, url: String) = "https://manager.$host/v3/$AUTOLOG_URL/?url=$url"

    private const val AUTOLOG_URL = "mobile_login"
    const val ACTIVITY_MANAGER_URL = "ng/profile/user/connection-history/activity"
    const val SETTINGS_MANAGER_URL = "ng/profile/user/security-and-recovery-parameters/dashboard"
}
