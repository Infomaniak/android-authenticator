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
package com.infomaniak.auth.ui.screen.main

import com.infomaniak.core.inappreview.BaseInAppReviewManager
import com.infomaniak.core.inappreview.reviewmanagers.InAppReviewManager
import com.infomaniak.core.inappupdate.updatemanagers.InAppUpdateManager

interface InAppServiceManager {
    val inAppReviewManager: InAppReviewManager
    val inAppUpdateManager: InAppUpdateManager

    fun initAppReviewManager() = inAppReviewManager.init(
        countdownBehavior = BaseInAppReviewManager.Behavior.Manual,
        appReviewThreshold = APP_REVIEW_THRESHOLD,
        maxAppReviewThreshold = MAX_APP_REVIEW_THRESHOLD,
    )

    fun initAppUpdateManager(isUpdateRequired: Boolean) = inAppUpdateManager.init(
        isUpdateRequired = isUpdateRequired,
    )

    companion object {
        private const val APP_REVIEW_THRESHOLD = 2
        private const val MAX_APP_REVIEW_THRESHOLD = 10
    }
}
