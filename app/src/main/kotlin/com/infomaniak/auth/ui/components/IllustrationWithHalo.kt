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
package com.infomaniak.auth.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.shieldPerson.ShieldPerson
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewLightAndDark
import com.infomaniak.core.ui.compose.theme.ThemedImage

@Composable
fun IllustrationWithHalo(
    themedImage: ThemedImage,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .size(350.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(AuthenticatorTheme.colors.illustrationBackgroundGradient, Color.Transparent),
                )
            ),
        imageVector = themedImage.image(),
        contentDescription = null,
    )
}

@PreviewLightAndDark
@Composable
fun IllustrationWithHaloPreview() {
    AuthenticatorTheme {
        Surface {
            IllustrationWithHalo(
                themedImage = AppImages.AppIllus.ShieldPerson,
                modifier = Modifier.padding(Margin.Large)
            )
        }
    }
}
