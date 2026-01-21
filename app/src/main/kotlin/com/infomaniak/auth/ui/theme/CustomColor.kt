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
package com.infomaniak.auth.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorScheme(
    val illustrationBackgroundGradient: Color = Color.Unspecified
)

val illustrationBackgroundGradientLight = primaryLight.copy(alpha = 0.28f)
val illustrationBackgroundGradientDark = primaryDark.copy(alpha = 0.6f)


fun lightColorScheme(
    illustrationBackgroundGradient: Color = illustrationBackgroundGradientLight
) = CustomColorScheme(
    illustrationBackgroundGradient = illustrationBackgroundGradient
)

fun darkColorScheme(
    illustrationBackgroundGradient: Color = illustrationBackgroundGradientDark
) = CustomColorScheme(
    illustrationBackgroundGradient = illustrationBackgroundGradient
)
