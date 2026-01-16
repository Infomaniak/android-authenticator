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
package com.infomaniak.auth.ui.images.illus.blueBlur

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.AppImages.AppIllus

@Suppress("UnusedReceiverParameter")
val AppIllus.BlueBlurDark: ImageVector
    get() {
        if (_blueBlurDark != null) {
            return _blueBlurDark!!
        }
        _blueBlurDark = Builder(
            name = "BlueBlurDark",
            defaultWidth = 460.0.dp,
            defaultHeight = 460.0.dp,
            viewportWidth = 460.0f,
            viewportHeight = 460.0f,
        ).apply {
            group {
                path(
                    fill = SolidColor(Color(0xFF5869D9)),
                    stroke = null,
                    fillAlpha = 0.256f,
                    strokeAlpha = 0.32f,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(364.18f, 230.0f)
                    curveTo(364.18f, 304.11f, 304.11f, 364.18f, 230.0f, 364.18f)
                    curveTo(155.89f, 364.18f, 95.82f, 304.11f, 95.82f, 230.0f)
                    curveTo(95.82f, 155.89f, 155.89f, 95.82f, 230.0f, 95.82f)
                    curveTo(304.11f, 95.82f, 364.18f, 155.89f, 364.18f, 230.0f)
                    close()
                }
            }
        }.build()
        return _blueBlurDark!!
    }

private var _blueBlurDark: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box {
        Image(
            imageVector = AppIllus.BlueBlurDark,
            contentDescription = null,
            modifier = Modifier.size(AppImages.previewSize),
        )
    }
}
