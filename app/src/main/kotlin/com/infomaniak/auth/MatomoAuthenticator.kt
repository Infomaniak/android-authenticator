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
package com.infomaniak.auth

import com.infomaniak.auth.lib.matomo.MatomoCategory
import com.infomaniak.auth.lib.matomo.MatomoName
import com.infomaniak.core.matomo.Matomo
import com.infomaniak.core.matomo.Matomo.TrackerAction
import org.matomo.sdk.Tracker

object MatomoAuthenticator : Matomo {
    override val tracker: Tracker by lazy(::buildTracker)
    override val siteId: Int = 40

    //region Track global events
    fun trackEvent(
        category: MatomoCategory,
        name: MatomoName,
        action: TrackerAction = TrackerAction.CLICK,
        value: Float? = null,
    ) {
        trackEvent(category.value, name.value, action, value)
    }
    //endregion

    //region Track specific events
    fun trackAccountEvent(name: MatomoName) {
        trackEvent(MatomoCategory.Account, name)
    }
    //endregion
}
