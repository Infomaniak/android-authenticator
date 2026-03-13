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

val AppIllus.PadlockLight: ImageVector
    get() {
        if (_padlockLight != null) {
            return _padlockLight!!
        }
        _padlockLight = Builder(
            name = "PadlockLight",
            defaultWidth = 302.0.dp,
            defaultHeight = 302.0.dp,
            viewportWidth = 302.0f,
            viewportHeight = 302.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = SolidColor(Color(0xFFffffff)),
                fillAlpha = 0.3f,
                strokeLineWidth = 2.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(97.06f, 101.57f)
                lineTo(204.94f, 101.57f)
                arcTo(9.08f, 9.08f, 0.0f, false, true, 214.03f, 110.66f)
                lineTo(214.03f, 223.02f)
                arcTo(9.08f, 9.08f, 0.0f, false, true, 204.94f, 232.1f)
                lineTo(97.06f, 232.1f)
                arcTo(9.08f, 9.08f, 0.0f, false, true, 87.97f, 223.02f)
                lineTo(87.97f, 110.66f)
                arcTo(9.08f, 9.08f, 0.0f, false, true, 97.06f, 101.57f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2F40AB)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(151.09f, 148.97f)
                curveTo(157.31f, 148.97f, 162.36f, 154.01f, 162.36f, 160.23f)
                curveTo(162.36f, 164.57f, 159.9f, 168.33f, 156.31f, 170.21f)
                verticalLineTo(179.03f)
                curveTo(156.31f, 181.91f, 153.97f, 184.25f, 151.1f, 184.25f)
                curveTo(148.22f, 184.25f, 145.88f, 181.91f, 145.88f, 179.03f)
                verticalLineTo(170.21f)
                curveTo(142.29f, 168.33f, 139.83f, 164.57f, 139.83f, 160.23f)
                curveTo(139.83f, 154.01f, 144.87f, 148.97f, 151.09f, 148.97f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = SolidColor(Color(0xFFffffff)),
                fillAlpha = 0.5f,
                strokeLineWidth = 2.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(150.51f, 38.52f)
                curveTo(176.97f, 38.52f, 198.43f, 59.98f, 198.43f, 86.44f)
                verticalLineTo(101.63f)
                horizontalLineTo(185.43f)
                verticalLineTo(86.44f)
                curveTo(185.43f, 67.16f, 169.79f, 51.52f, 150.51f, 51.52f)
                curveTo(131.22f, 51.52f, 115.58f, 67.16f, 115.58f, 86.44f)
                verticalLineTo(101.64f)
                horizontalLineTo(102.58f)
                verticalLineTo(86.44f)
                curveTo(102.58f, 59.98f, 124.04f, 38.52f, 150.51f, 38.52f)
                close()
            }
        }.build()
        return _padlockLight!!
    }

private var _padlockLight: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box {
        Image(
            imageVector = AppIllus.PadlockLight,
            contentDescription = null,
            modifier = Modifier.size(AppImages.previewSize),
        )
    }
}
