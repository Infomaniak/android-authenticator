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
data class CustomColorScheme(
    val illustrationBackgroundGradient: Color = Color.Unspecified,
    val accountSecured: Color = Color.Unspecified,
    val iconTintWarning: Color = Color.Unspecified,
    val iconTintError: Color = Color.Unspecified,
    val accountItemBackground: Color = Color.Unspecified,
    val optionsSectionBackground: Color = Color.Unspecified,
    val accountDisconnected: Color = Color.Unspecified,
)

private val accountSecuredLight = successDim5Light
private val accountSecuredDark = successDim5Dark
private val iconTintWarningLight = warningDim5Light
private val iconTintWarningDark = warningDim5Dark
private val iconTintErrorLight = errorDim5Light
private val iconTintErrorDark = errorDim5Dark
private val accountItemBackgroundLight = neutral100
private val accountItemBackgroundDark = backgroundSurface
private val optionsSectionBackgroundLight = neutral100
private val optionsSectionBackgroundDark = backgroundSurface
private val accountDisconnectedLight = neutralDim4
private val accountDisconnectedDark = neutralDim4

val lightCustomScheme = CustomColorScheme(
    accountSecured = accountSecuredLight,
    iconTintWarning = iconTintWarningLight,
    iconTintError = iconTintErrorLight,
    accountItemBackground = accountItemBackgroundLight,
    optionsSectionBackground = optionsSectionBackgroundLight,
    accountDisconnected = accountDisconnectedLight,
)

val darkCustomScheme = CustomColorScheme(
    accountSecured = accountSecuredDark,
    iconTintWarning = iconTintWarningDark,
    iconTintError = iconTintErrorDark,
    accountItemBackground = accountItemBackgroundDark,
    optionsSectionBackground = optionsSectionBackgroundDark,
    accountDisconnected = accountDisconnectedDark,
)
