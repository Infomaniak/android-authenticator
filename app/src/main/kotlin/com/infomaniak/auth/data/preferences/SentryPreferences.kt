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
@file:OptIn(ExperimentalSplittiesApi::class)

package com.infomaniak.auth.data.preferences

import kotlinx.coroutines.flow.Flow
import splitties.experimental.ExperimentalSplittiesApi
import splitties.preferences.Preferences
import splitties.preferences.SuspendPrefsAccessor

class SentryPreferences private constructor(): Preferences(name = "SentryPreferences") {
    companion object : SuspendPrefsAccessor<SentryPreferences>(::SentryPreferences)

    val isSentryAuthorizedFlow : Flow<Boolean>
    var isSentryAuthorized by boolPref(key = "IsSentryAuthorized", defaultValue = true).also {
        isSentryAuthorizedFlow = it.valueFlow()
    }
}
