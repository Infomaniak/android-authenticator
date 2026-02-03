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
    val illustrationBackgroundGradient: Color = Color.Unspecified,
    val actionRequiredPrimary: Color = Color.Unspecified,
    val actionRequiredBackground: Color = Color.Unspecified,
    val accountSecured: Color = Color.Unspecified,
    val accountWarning: Color = Color.Unspecified,
    val accountItemBackground: Color = Color.Unspecified,
    val optionsSectionBackground: Color = Color.Unspecified,
)

private val illustrationBackgroundGradientLight = lightScheme.primary.copy(alpha = 0.28f)
private val illustrationBackgroundGradientDark = productSecurity.copy(alpha = 0.6f)
private val actionRequiredPrimaryLight = warningDim5Light
private val actionRequiredPrimaryDark = warningDim5Dark
private val actionRequiredBackgroundLight = warningDim4Light
private val actionRequiredBackgroundDark = warningDim4Dark
private val accountSecuredLight = successDim5Light
private val accountSecuredDark = successDim5Dark
private val accountWarningLight = warningDim5Light
private val accountWarningDark = warningDim5Dark
private val accountItemBackgroundLight = neutral100
private val accountItemBackgroundDark = backgroundSurface
private val optionsSectionBackgroundLight = neutral100
private val optionsSectionBackgroundDark = backgroundSurface

val lightCustomScheme = CustomColorScheme(
    illustrationBackgroundGradient = illustrationBackgroundGradientLight,
    actionRequiredPrimary = actionRequiredPrimaryLight,
    actionRequiredBackground = actionRequiredBackgroundLight,
    accountSecured = accountSecuredLight,
    accountWarning = accountWarningLight,
    accountItemBackground = accountItemBackgroundLight,
    optionsSectionBackground = optionsSectionBackgroundLight,
)

val darkCustomScheme = CustomColorScheme(
    illustrationBackgroundGradient = illustrationBackgroundGradientDark,
    actionRequiredPrimary = actionRequiredPrimaryDark,
    actionRequiredBackground = actionRequiredBackgroundDark,
    accountSecured = accountSecuredDark,
    accountWarning = accountWarningDark,
    accountItemBackground = accountItemBackgroundDark,
    optionsSectionBackground = optionsSectionBackgroundDark,
)
