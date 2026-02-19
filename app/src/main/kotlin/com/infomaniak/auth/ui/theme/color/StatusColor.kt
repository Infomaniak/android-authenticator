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
package com.infomaniak.auth.ui.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class StatusColorScheme(
    val warning: Color = Color.Unspecified,
    val onWarning: Color = Color.Unspecified,
    val warningContainer: Color = Color.Unspecified,
    val onWarningContainer: Color = Color.Unspecified,

    val error: Color = Color.Unspecified,
    val onError: Color = Color.Unspecified,
    val errorContainer: Color = Color.Unspecified,
    val onErrorContainer: Color = Color.Unspecified,
)

val lightStatusColorScheme = StatusColorScheme(
    warning = orange40,
    onWarning = orange99,
    warningContainer = orange80,
    onWarningContainer = orange15,

    error = red40,
    onError = red99,
    errorContainer = red80,
    onErrorContainer = red15,
)

val darkStatusColorScheme = StatusColorScheme(
    warning = orange80,
    onWarning = orange20,
    warningContainer = orange20,
    onWarningContainer = orange95,

    error = red80,
    onError = red20,
    errorContainer = red20,
    onErrorContainer = red95,
)
