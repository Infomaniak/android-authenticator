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
    val warningLow: Color = Color.Unspecified,
    val warningMedium: Color = Color.Unspecified,
    val warningHigh: Color = Color.Unspecified,
    val warningHighest: Color = Color.Unspecified,

    val errorLow: Color = Color.Unspecified,
    val errorMedium: Color = Color.Unspecified,
    val errorHigh: Color = Color.Unspecified,
    val errorHighest: Color = Color.Unspecified,
)

val lightStatusColorScheme = StatusColorScheme(
    warningLow = warningDim1Light,
    warningMedium = warningDim3Light,
    warningHigh = warningDim5Light,
    warningHighest = warningDim6Light,

    errorLow = errorDim1Light,
    errorMedium = errorDim3Light,
    errorHigh = errorDim5Light,
    errorHighest = errorDim6Light,
)

val darkStatusColorScheme = StatusColorScheme(
    warningLow = warningDim1Dark,
    warningMedium = warningDim3Dark,
    warningHigh = warningDim5Dark,
    warningHighest = warningDim6Dark,

    errorLow = errorDim1Dark,
    errorMedium = errorDim3Dark,
    errorHigh = errorDim5Dark,
    errorHighest = errorDim6Dark,
)
