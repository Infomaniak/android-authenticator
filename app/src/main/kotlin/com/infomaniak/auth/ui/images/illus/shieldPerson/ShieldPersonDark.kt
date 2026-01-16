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
package com.infomaniak.auth.ui.images.illus.shieldPerson

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
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

@Suppress("UnusedReceiverParameter")
val AppIllus.ShieldPersonDark: ImageVector
    get() {
        if (_shieldPersonDark != null) {
            return _shieldPersonDark!!
        }
        _shieldPersonDark = Builder(
            name = "ShieldPersonDark",
            defaultWidth = 302.0.dp,
            defaultHeight = 302.0.dp,
            viewportWidth = 302.0f,
            viewportHeight = 302.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF5869D9)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(226.98f, 101.35f)
                horizontalLineTo(227.98f)
                verticalLineTo(100.73f)
                lineTo(227.42f, 100.46f)
                lineTo(226.98f, 101.35f)
                close()
                moveTo(151.0f, 237.58f)
                lineTo(150.64f, 238.5f)
                lineTo(151.0f, 238.64f)
                lineTo(151.36f, 238.5f)
                lineTo(151.0f, 237.58f)
                close()
                moveTo(75.02f, 101.35f)
                lineTo(74.58f, 100.46f)
                lineTo(74.03f, 100.73f)
                lineTo(74.03f, 101.35f)
                horizontalLineTo(75.02f)
                close()
                moveTo(151.0f, 64.42f)
                lineTo(151.43f, 63.53f)
                lineTo(151.0f, 63.32f)
                lineTo(150.57f, 63.53f)
                lineTo(151.0f, 64.42f)
                close()
                moveTo(226.98f, 101.35f)
                horizontalLineTo(225.99f)
                curveTo(225.99f, 123.14f, 220.88f, 151.6f, 208.87f, 177.31f)
                curveTo(196.87f, 203.02f, 178.02f, 225.88f, 150.64f, 236.66f)
                lineTo(151.0f, 237.58f)
                lineTo(151.36f, 238.5f)
                curveTo(179.38f, 227.48f, 198.53f, 204.14f, 210.66f, 178.15f)
                curveTo(222.8f, 152.15f, 227.98f, 123.4f, 227.98f, 101.35f)
                horizontalLineTo(226.98f)
                close()
                moveTo(151.0f, 237.58f)
                lineTo(151.36f, 236.66f)
                curveTo(123.98f, 225.88f, 105.14f, 203.02f, 93.13f, 177.31f)
                curveTo(81.13f, 151.6f, 76.01f, 123.14f, 76.01f, 101.35f)
                horizontalLineTo(75.02f)
                horizontalLineTo(74.03f)
                curveTo(74.03f, 123.4f, 79.2f, 152.15f, 91.34f, 178.15f)
                curveTo(103.47f, 204.14f, 122.63f, 227.48f, 150.64f, 238.5f)
                lineTo(151.0f, 237.58f)
                close()
                moveTo(75.02f, 101.35f)
                lineTo(75.45f, 102.24f)
                lineTo(151.43f, 65.31f)
                lineTo(151.0f, 64.42f)
                lineTo(150.57f, 63.53f)
                lineTo(74.58f, 100.46f)
                lineTo(75.02f, 101.35f)
                close()
                moveTo(151.0f, 64.42f)
                lineTo(150.57f, 65.31f)
                lineTo(226.55f, 102.24f)
                lineTo(226.98f, 101.35f)
                lineTo(227.42f, 100.46f)
                lineTo(151.43f, 63.53f)
                lineTo(151.0f, 64.42f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF5869D9)),
                stroke = null,
                fillAlpha = 0.2f,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(151.0f, 237.58f)
                verticalLineTo(64.42f)
                lineTo(226.99f, 101.35f)
                curveTo(226.99f, 145.19f, 206.4f, 215.78f, 151.0f, 237.58f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF5869D9)),
                stroke = null,
                fillAlpha = 0.6f,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(151.0f, 237.58f)
                verticalLineTo(64.42f)
                lineTo(75.02f, 101.35f)
                curveTo(75.02f, 145.19f, 95.6f, 215.78f, 151.0f, 237.58f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF3243AE)),
                stroke = null,
                fillAlpha = 0.3f,
                strokeAlpha = 0.3f,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(151.41f, 150.91f)
                moveToRelative(-30.35f, 0.0f)
                arcToRelative(30.35f, 30.35f, 0.0f, true, true, 60.7f, 0.0f)
                arcToRelative(30.35f, 30.35f, 0.0f, true, true, -60.7f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFF5869D9)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(176.4f, 150.89f)
                curveTo(176.4f, 164.69f, 165.21f, 175.88f, 151.41f, 175.88f)
                curveTo(137.61f, 175.88f, 126.42f, 164.69f, 126.42f, 150.89f)
                curveTo(126.42f, 137.09f, 137.61f, 125.9f, 151.41f, 125.9f)
                curveTo(165.21f, 125.9f, 176.4f, 137.09f, 176.4f, 150.89f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = EvenOdd,
            ) {
                moveTo(145.45f, 138.36f)
                curveTo(147.03f, 136.78f, 149.17f, 135.89f, 151.41f, 135.89f)
                curveTo(153.65f, 135.89f, 155.79f, 136.78f, 157.37f, 138.36f)
                curveTo(158.95f, 139.95f, 159.84f, 142.09f, 159.84f, 144.33f)
                curveTo(159.84f, 146.57f, 158.95f, 148.71f, 157.37f, 150.29f)
                curveTo(156.4f, 151.26f, 155.22f, 151.97f, 153.94f, 152.37f)
                curveTo(156.47f, 152.86f, 158.82f, 154.07f, 160.68f, 155.88f)
                curveTo(163.14f, 158.28f, 164.53f, 161.54f, 164.53f, 164.95f)
                curveTo(164.53f, 165.19f, 164.43f, 165.43f, 164.25f, 165.61f)
                curveTo(164.08f, 165.78f, 163.84f, 165.88f, 163.59f, 165.88f)
                horizontalLineTo(139.23f)
                curveTo(138.98f, 165.88f, 138.74f, 165.78f, 138.56f, 165.61f)
                curveTo(138.39f, 165.43f, 138.29f, 165.19f, 138.29f, 164.95f)
                curveTo(138.29f, 161.54f, 139.68f, 158.28f, 142.14f, 155.88f)
                curveTo(144.0f, 154.07f, 146.35f, 152.86f, 148.88f, 152.37f)
                curveTo(147.6f, 151.97f, 146.41f, 151.26f, 145.45f, 150.29f)
                curveTo(143.86f, 148.71f, 142.97f, 146.57f, 142.97f, 144.33f)
                curveTo(142.97f, 142.09f, 143.86f, 139.95f, 145.45f, 138.36f)
                close()
                moveTo(151.41f, 137.77f)
                curveTo(149.67f, 137.77f, 148.0f, 138.46f, 146.77f, 139.69f)
                curveTo(145.54f, 140.92f, 144.85f, 142.59f, 144.85f, 144.33f)
                curveTo(144.85f, 146.07f, 145.54f, 147.74f, 146.77f, 148.97f)
                curveTo(148.0f, 150.2f, 149.67f, 150.89f, 151.41f, 150.89f)
                curveTo(153.15f, 150.89f, 154.82f, 150.2f, 156.05f, 148.97f)
                curveTo(157.28f, 147.74f, 157.97f, 146.07f, 157.97f, 144.33f)
                curveTo(157.97f, 142.59f, 157.28f, 140.92f, 156.05f, 139.69f)
                curveTo(154.82f, 138.46f, 153.15f, 137.77f, 151.41f, 137.77f)
                close()
                moveTo(151.41f, 154.01f)
                curveTo(148.42f, 154.01f, 145.55f, 155.17f, 143.45f, 157.22f)
                curveTo(141.57f, 159.05f, 140.43f, 161.46f, 140.2f, 164.01f)
                horizontalLineTo(162.61f)
                curveTo(162.39f, 161.46f, 161.25f, 159.05f, 159.37f, 157.22f)
                curveTo(157.26f, 155.17f, 154.4f, 154.01f, 151.41f, 154.01f)
                close()
            }
        }.build()
        return _shieldPersonDark!!
    }

private var _shieldPersonDark: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box {
        Image(
            imageVector = AppIllus.ShieldPersonDark,
            contentDescription = null,
            modifier = Modifier.size(AppImages.previewSize),
        )
    }
}
