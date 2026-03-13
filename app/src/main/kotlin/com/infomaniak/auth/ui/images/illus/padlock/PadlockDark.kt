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
package com.infomaniak.auth.ui.images.illus.padlock

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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.AppImages.AppIllus

val AppIllus.PadlockDark: ImageVector
    get() {
        if (_padlockDark != null) {
            return _padlockDark!!
        }
        _padlockDark = Builder(
            name = "PadlockDark",
            defaultWidth = 302.0.dp,
            defaultHeight = 302.0.dp,
            viewportWidth = 302.0f,
            viewportHeight = 302.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF5869D9)),
                stroke = SolidColor(Color(0xFF5869D9)),
                fillAlpha = 0.3f,
                strokeLineWidth = 2.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(96.56f, 101.57f)
                lineTo(204.44f, 101.57f)
                arcTo(9.08f, 9.08f, 0.0f, false, true, 213.53f, 110.66f)
                lineTo(213.53f, 223.02f)
                arcTo(9.08f, 9.08f, 0.0f, false, true, 204.44f, 232.1f)
                lineTo(96.56f, 232.1f)
                arcTo(9.08f, 9.08f, 0.0f, false, true, 87.47f, 223.02f)
                lineTo(87.47f, 110.66f)
                arcTo(9.08f, 9.08f, 0.0f, false, true, 96.56f, 101.57f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(150.59f, 148.97f)
                curveTo(156.81f, 148.97f, 161.86f, 154.01f, 161.86f, 160.23f)
                curveTo(161.86f, 164.57f, 159.4f, 168.33f, 155.81f, 170.21f)
                verticalLineTo(179.03f)
                curveTo(155.81f, 181.91f, 153.47f, 184.25f, 150.6f, 184.25f)
                curveTo(147.72f, 184.25f, 145.38f, 181.91f, 145.38f, 179.03f)
                verticalLineTo(170.21f)
                curveTo(141.79f, 168.33f, 139.33f, 164.57f, 139.33f, 160.23f)
                curveTo(139.33f, 154.01f, 144.37f, 148.97f, 150.59f, 148.97f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF5869D9)),
                stroke = SolidColor(Color(0xFF5869D9)),
                fillAlpha = 0.5f,
                strokeLineWidth = 2.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(150.01f, 38.52f)
                curveTo(176.47f, 38.52f, 197.93f, 59.98f, 197.93f, 86.44f)
                verticalLineTo(101.63f)
                horizontalLineTo(184.93f)
                verticalLineTo(86.44f)
                curveTo(184.93f, 67.16f, 169.29f, 51.52f, 150.01f, 51.52f)
                curveTo(130.72f, 51.52f, 115.08f, 67.16f, 115.08f, 86.44f)
                verticalLineTo(101.64f)
                horizontalLineTo(102.08f)
                verticalLineTo(86.44f)
                curveTo(102.08f, 59.98f, 123.54f, 38.52f, 150.01f, 38.52f)
                close()
            }
        }.build()
        return _padlockDark!!
    }

private var _padlockDark: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box {
        Image(
            imageVector = AppIllus.PadlockDark,
            contentDescription = null,
            modifier = Modifier.size(AppImages.previewSize),
        )
    }
}
