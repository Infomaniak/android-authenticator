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
package com.infomaniak.auth.ui.images.illus.dataProtection

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
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.AppImages.AppIllus

val AppIllus.DataProtectionDark: ImageVector
    get() {
        if (_dataProtectionDark != null) {
            return _dataProtectionDark!!
        }
        _dataProtectionDark = Builder(
            name = "DataProtectionDark",
            defaultWidth = 100.0.dp,
            defaultHeight = 93.0.dp,
            viewportWidth = 100.0f,
            viewportHeight = 93.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF5869D9)),
                stroke = SolidColor(Color(0xFFE0E0E0)),
                fillAlpha = 0.4f,
                strokeLineWidth = 0.5f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(70.9f, 48.2f)
                lineTo(70.92f, 48.43f)
                lineTo(73.59f, 91.8f)
                lineTo(73.61f, 92.07f)
                horizontalLineTo(15.52f)
                lineTo(15.54f, 91.8f)
                lineTo(18.22f, 48.43f)
                lineTo(18.23f, 48.2f)
                horizontalLineTo(70.9f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF7583E3)),
                strokeLineWidth = 0.5f,
                strokeLineCap = Round,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(42.84f, 61.71f)
                curveTo(42.84f, 65.09f, 44.1f, 68.34f, 45.29f, 71.48f)
                curveTo(45.81f, 72.84f, 46.47f, 74.25f, 46.58f, 75.72f)
                curveTo(46.62f, 76.34f, 46.86f, 77.34f, 46.53f, 77.9f)
                curveTo(45.94f, 78.92f, 43.14f, 78.68f, 42.28f, 78.66f)
                curveTo(39.51f, 78.6f, 36.68f, 78.12f, 34.11f, 77.07f)
                curveTo(33.53f, 76.83f, 32.46f, 76.45f, 32.81f, 75.63f)
                curveTo(33.32f, 74.44f, 35.33f, 73.69f, 36.37f, 73.17f)
                curveTo(39.73f, 71.48f, 43.19f, 70.29f, 46.91f, 69.66f)
                curveTo(48.63f, 69.37f, 50.41f, 68.98f, 52.16f, 68.91f)
                curveTo(52.44f, 68.89f, 53.39f, 68.8f, 52.53f, 68.95f)
                curveTo(47.75f, 69.79f, 42.99f, 70.81f, 38.22f, 71.69f)
                curveTo(35.54f, 72.18f, 32.86f, 72.64f, 30.17f, 73.06f)
                curveTo(29.36f, 73.19f, 26.93f, 73.61f, 27.72f, 73.38f)
                curveTo(29.76f, 72.81f, 31.72f, 71.9f, 33.72f, 71.24f)
                curveTo(39.21f, 69.42f, 45.07f, 68.12f, 50.8f, 67.36f)
                curveTo(54.25f, 66.9f, 57.72f, 66.68f, 61.17f, 66.28f)
                curveTo(62.09f, 66.17f, 63.04f, 66.0f, 63.97f, 66.0f)
            }
            path(
                fill = SolidColor(Color(0xFF5869D9)),
                stroke = SolidColor(Color(0xFFE0E0E0)),
                fillAlpha = 0.4f,
                strokeLineWidth = 0.5f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(62.92f, 12.66f)
                lineTo(63.0f, 12.74f)
                lineTo(72.68f, 23.16f)
                lineTo(72.76f, 23.24f)
                lineTo(72.75f, 23.35f)
                lineTo(70.86f, 47.93f)
                lineTo(70.84f, 48.16f)
                horizontalLineTo(18.27f)
                lineTo(18.25f, 47.93f)
                lineTo(15.25f, 12.93f)
                lineTo(15.23f, 12.66f)
                horizontalLineTo(62.92f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDCE4E5)),
                stroke = SolidColor(Color(0xFFE0E0E0)),
                strokeLineWidth = 0.5f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(63.36f, 13.61f)
                lineTo(72.18f, 22.73f)
                lineTo(72.59f, 23.16f)
                horizontalLineTo(61.2f)
                lineTo(61.25f, 22.86f)
                lineTo(62.93f, 13.74f)
                lineTo(63.02f, 13.26f)
                lineTo(63.36f, 13.61f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDCE4E5)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(26.23f, 25.43f)
                lineTo(52.2f, 25.43f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 53.27f, 26.5f)
                lineTo(53.27f, 26.5f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 52.2f, 27.57f)
                lineTo(26.23f, 27.57f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 25.16f, 26.5f)
                lineTo(25.16f, 26.5f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 26.23f, 25.43f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDCE4E5)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(26.23f, 30.78f)
                lineTo(62.1f, 30.78f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 63.17f, 31.85f)
                lineTo(63.17f, 31.85f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 62.1f, 32.92f)
                lineTo(26.23f, 32.92f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 25.16f, 31.85f)
                lineTo(25.16f, 31.85f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 26.23f, 30.78f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDCE4E5)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(26.23f, 36.13f)
                lineTo(62.1f, 36.13f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 63.17f, 37.2f)
                lineTo(63.17f, 37.2f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 62.1f, 38.27f)
                lineTo(26.23f, 38.27f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 25.16f, 37.2f)
                lineTo(25.16f, 37.2f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 26.23f, 36.13f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDCE4E5)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(26.23f, 41.49f)
                lineTo(62.1f, 41.49f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 63.17f, 42.56f)
                lineTo(63.17f, 42.56f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 62.1f, 43.63f)
                lineTo(26.23f, 43.63f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 25.16f, 42.56f)
                lineTo(25.16f, 42.56f)
                arcTo(1.07f, 1.07f, 0.0f, false, true, 26.23f, 41.49f)
                close()
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
                moveTo(87.86f, 49.52f)
                lineToRelative(5.45f, 6.19f)
                lineToRelative(-20.06f, 17.66f)
                lineToRelative(-5.45f, -6.19f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF3243AE)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(89.61f, 51.5f)
                lineToRelative(1.96f, 2.23f)
                lineToRelative(-20.06f, 17.66f)
                lineToRelative(-1.96f, -2.23f)
                close()
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
                moveTo(92.57f, 45.37f)
                curveTo(93.66f, 44.41f, 95.33f, 44.52f, 96.29f, 45.61f)
                lineTo(98.26f, 47.84f)
                curveTo(99.22f, 48.93f, 99.11f, 50.6f, 98.02f, 51.56f)
                lineTo(93.81f, 55.27f)
                lineTo(88.36f, 49.08f)
                lineTo(92.57f, 45.37f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF1F1F1)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(63.26f, 77.48f)
                curveTo(62.7f, 77.68f, 62.19f, 77.1f, 62.45f, 76.57f)
                lineTo(66.56f, 68.27f)
                lineTo(69.29f, 71.36f)
                lineTo(69.29f, 71.36f)
                lineTo(67.87f, 69.75f)
                curveTo(68.28f, 69.39f, 68.91f, 69.43f, 69.27f, 69.84f)
                lineTo(69.38f, 69.96f)
                curveTo(69.74f, 70.37f, 69.7f, 70.99f, 69.3f, 71.35f)
                curveTo(69.71f, 71.0f, 70.33f, 71.04f, 70.69f, 71.45f)
                lineTo(70.8f, 71.57f)
                curveTo(71.16f, 71.98f, 71.12f, 72.61f, 70.71f, 72.97f)
                lineTo(72.01f, 74.46f)
                lineTo(63.26f, 77.48f)
                close()
                moveTo(67.96f, 68.35f)
                curveTo(68.32f, 68.76f, 68.28f, 69.39f, 67.87f, 69.75f)
                lineTo(66.57f, 68.26f)
                curveTo(66.98f, 67.9f, 67.6f, 67.94f, 67.96f, 68.35f)
                close()
                moveTo(72.1f, 73.06f)
                curveTo(72.47f, 73.47f, 72.43f, 74.1f, 72.02f, 74.46f)
                lineTo(70.71f, 72.97f)
                curveTo(71.12f, 72.61f, 71.74f, 72.65f, 72.1f, 73.06f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF7583E3)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(63.88f, 73.71f)
                lineTo(66.28f, 76.43f)
                lineTo(63.19f, 77.55f)
                curveTo(62.63f, 77.75f, 62.11f, 77.16f, 62.38f, 76.63f)
                lineTo(63.88f, 73.71f)
                close()
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
                moveTo(16.21f, 16.21f)
                moveToRelative(-14.4f, 1.81f)
                arcToRelative(14.51f, 14.51f, 127.83f, true, true, 28.79f, -3.62f)
                arcToRelative(14.51f, 14.51f, 127.83f, true, true, -28.79f, 3.62f)
            }
            group {
                path(
                    fill = SolidColor(Color(0xFFffffff)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(15.3f, 9.01f)
                    curveTo(15.55f, 8.98f, 15.78f, 9.15f, 15.81f, 9.4f)
                    lineTo(15.92f, 10.3f)
                    curveTo(15.95f, 10.55f, 15.78f, 10.78f, 15.53f, 10.81f)
                    curveTo(15.28f, 10.84f, 15.05f, 10.66f, 15.02f, 10.41f)
                    lineTo(14.91f, 9.51f)
                    curveTo(14.88f, 9.27f, 15.05f, 9.04f, 15.3f, 9.01f)
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
                    moveTo(14.39f, 19.94f)
                    curveTo(13.69f, 19.65f, 13.07f, 19.19f, 12.59f, 18.59f)
                    curveTo(12.07f, 17.94f, 11.74f, 17.15f, 11.65f, 16.32f)
                    lineTo(11.65f, 16.32f)
                    curveTo(11.52f, 15.13f, 11.86f, 13.94f, 12.59f, 12.99f)
                    curveTo(13.32f, 12.05f, 14.39f, 11.43f, 15.58f, 11.26f)
                    curveTo(15.58f, 11.26f, 15.59f, 11.26f, 15.59f, 11.26f)
                    curveTo(16.78f, 11.13f, 17.97f, 11.47f, 18.91f, 12.2f)
                    curveTo(19.86f, 12.93f, 20.48f, 14.0f, 20.65f, 15.18f)
                    lineTo(20.65f, 15.18f)
                    curveTo(20.76f, 16.01f, 20.64f, 16.86f, 20.3f, 17.62f)
                    curveTo(19.99f, 18.32f, 19.5f, 18.92f, 18.89f, 19.37f)
                    lineTo(19.19f, 21.77f)
                    curveTo(19.24f, 22.13f, 19.14f, 22.49f, 18.92f, 22.78f)
                    curveTo(18.69f, 23.06f, 18.37f, 23.25f, 18.01f, 23.29f)
                    lineTo(16.21f, 23.52f)
                    curveTo(15.85f, 23.56f, 15.49f, 23.46f, 15.21f, 23.24f)
                    curveTo(14.92f, 23.02f, 14.74f, 22.7f, 14.69f, 22.34f)
                    lineTo(14.39f, 19.94f)
                    close()
                    moveTo(15.31f, 19.97f)
                    lineTo(15.42f, 20.87f)
                    lineTo(18.12f, 20.53f)
                    lineTo(18.01f, 19.64f)
                    lineTo(15.31f, 19.97f)
                    close()
                    moveTo(15.54f, 21.77f)
                    lineTo(15.59f, 22.22f)
                    curveTo(15.61f, 22.34f, 15.67f, 22.45f, 15.76f, 22.53f)
                    curveTo(15.86f, 22.6f, 15.98f, 22.63f, 16.1f, 22.62f)
                    lineTo(17.9f, 22.39f)
                    curveTo(18.02f, 22.38f, 18.13f, 22.31f, 18.2f, 22.22f)
                    curveTo(18.27f, 22.12f, 18.31f, 22.0f, 18.29f, 21.88f)
                    lineTo(18.24f, 21.43f)
                    lineTo(15.54f, 21.77f)
                    close()
                    moveTo(13.31f, 13.55f)
                    curveTo(13.89f, 12.79f, 14.75f, 12.3f, 15.7f, 12.16f)
                    curveTo(16.65f, 12.06f, 17.6f, 12.33f, 18.36f, 12.91f)
                    curveTo(19.11f, 13.5f, 19.61f, 14.36f, 19.75f, 15.31f)
                    curveTo(19.84f, 15.97f, 19.75f, 16.64f, 19.47f, 17.25f)
                    curveTo(19.21f, 17.83f, 18.8f, 18.32f, 18.29f, 18.69f)
                    lineTo(14.8f, 19.12f)
                    curveTo(14.22f, 18.9f, 13.7f, 18.52f, 13.3f, 18.03f)
                    curveTo(12.89f, 17.51f, 12.63f, 16.88f, 12.55f, 16.22f)
                    curveTo(12.45f, 15.26f, 12.72f, 14.31f, 13.31f, 13.55f)
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
                    moveTo(9.8f, 16.1f)
                    curveTo(9.55f, 16.13f, 9.37f, 16.36f, 9.4f, 16.6f)
                    curveTo(9.43f, 16.85f, 9.66f, 17.03f, 9.91f, 17.0f)
                    lineTo(10.81f, 16.88f)
                    curveTo(11.06f, 16.85f, 11.23f, 16.63f, 11.2f, 16.38f)
                    curveTo(11.17f, 16.13f, 10.94f, 15.95f, 10.69f, 15.99f)
                    lineTo(9.8f, 16.1f)
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
                    moveTo(10.27f, 11.61f)
                    curveTo(10.42f, 11.41f, 10.71f, 11.37f, 10.91f, 11.52f)
                    lineTo(11.65f, 12.07f)
                    curveTo(11.85f, 12.22f, 11.89f, 12.5f, 11.74f, 12.7f)
                    curveTo(11.59f, 12.9f, 11.31f, 12.94f, 11.1f, 12.79f)
                    lineTo(10.36f, 12.24f)
                    curveTo(10.16f, 12.09f, 10.12f, 11.81f, 10.27f, 11.61f)
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
                    moveTo(21.49f, 14.63f)
                    curveTo(21.24f, 14.66f, 21.07f, 14.89f, 21.1f, 15.13f)
                    curveTo(21.13f, 15.38f, 21.36f, 15.56f, 21.6f, 15.53f)
                    lineTo(22.5f, 15.41f)
                    curveTo(22.75f, 15.38f, 22.93f, 15.16f, 22.9f, 14.91f)
                    curveTo(22.87f, 14.66f, 22.64f, 14.48f, 22.39f, 14.51f)
                    lineTo(21.49f, 14.63f)
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
                    moveTo(20.82f, 10.28f)
                    curveTo(21.01f, 10.44f, 21.04f, 10.73f, 20.89f, 10.92f)
                    lineTo(20.31f, 11.64f)
                    curveTo(20.15f, 11.83f, 19.86f, 11.86f, 19.67f, 11.7f)
                    curveTo(19.47f, 11.55f, 19.44f, 11.26f, 19.6f, 11.07f)
                    lineTo(20.18f, 10.35f)
                    curveTo(20.34f, 10.16f, 20.62f, 10.13f, 20.82f, 10.28f)
                    close()
                }
            }
        }.build()
        return _dataProtectionDark!!
    }

private var _dataProtectionDark: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box {
        Image(
            imageVector = AppIllus.DataProtectionDark,
            contentDescription = null,
            modifier = Modifier.size(AppImages.previewSize),
        )
    }
}
