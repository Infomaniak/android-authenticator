package com.infomaniak.auth.ui.images.illus.bannerNotification

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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.AppImages.AppIllus

val AppIllus.BannerNotificationLight: ImageVector
    get() {
        if (_bannerNotificationLight != null) {
            return _bannerNotificationLight!!
        }
        _bannerNotificationLight = Builder(
            name = "BannerNotificationLight",
            defaultWidth = 302.0.dp,
            defaultHeight = 302.0.dp,
            viewportWidth = 302.0f,
            viewportHeight = 302.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = SolidColor(Color(0xFFffffff)),
                fillAlpha = 0.4f,
                strokeLineWidth = 3.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(16.56f, 106.42f)
                lineTo(285.44f, 106.42f)
                arcTo(15.06f, 15.06f, 0.0f, false, true, 300.5f, 121.48f)
                lineTo(300.5f, 173.42f)
                arcTo(15.06f, 15.06f, 0.0f, false, true, 285.44f, 188.48f)
                lineTo(16.56f, 188.48f)
                arcTo(15.06f, 15.06f, 0.0f, false, true, 1.5f, 173.42f)
                lineTo(1.5f, 121.48f)
                arcTo(15.06f, 15.06f, 0.0f, false, true, 16.56f, 106.42f)
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
                moveTo(36.29f, 123.03f)
                lineTo(51.47f, 123.03f)
                arcTo(17.25f, 17.25f, 0.0f, false, true, 68.72f, 140.27f)
                lineTo(68.72f, 155.46f)
                arcTo(17.25f, 17.25f, 0.0f, false, true, 51.47f, 172.7f)
                lineTo(36.29f, 172.7f)
                arcTo(17.25f, 17.25f, 0.0f, false, true, 19.04f, 155.46f)
                lineTo(19.04f, 140.27f)
                arcTo(17.25f, 17.25f, 0.0f, false, true, 36.29f, 123.03f)
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
                moveTo(42.73f, 144.14f)
                verticalLineTo(151.76f)
                horizontalLineTo(45.91f)
                verticalLineTo(144.14f)
                horizontalLineTo(42.73f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = null,
                fillAlpha = 0.4f,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(53.98f, 145.2f)
                curveTo(53.98f, 139.73f, 49.57f, 135.31f, 44.15f, 135.31f)
                verticalLineTo(132.04f)
                curveTo(51.4f, 132.04f, 57.26f, 137.94f, 57.26f, 145.2f)
                verticalLineTo(150.39f)
                horizontalLineTo(53.98f)
                verticalLineTo(145.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = null,
                fillAlpha = 0.4f,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(41.23f, 132.36f)
                curveTo(35.4f, 133.69f, 31.04f, 138.87f, 31.04f, 145.14f)
                horizontalLineTo(34.31f)
                curveTo(34.31f, 140.44f, 37.58f, 136.56f, 41.96f, 135.56f)
                lineTo(41.23f, 132.36f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = null,
                fillAlpha = 0.4f,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(34.31f, 150.33f)
                verticalLineTo(147.77f)
                horizontalLineTo(31.04f)
                verticalLineTo(150.33f)
                curveTo(31.04f, 157.59f, 36.9f, 163.5f, 44.15f, 163.5f)
                curveTo(50.39f, 163.5f, 55.61f, 159.11f, 56.93f, 153.25f)
                lineTo(53.74f, 152.53f)
                curveTo(52.74f, 156.94f, 48.82f, 160.22f, 44.15f, 160.22f)
                curveTo(38.72f, 160.22f, 34.31f, 155.8f, 34.31f, 150.33f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = null,
                fillAlpha = 0.8f,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(44.15f, 137.8f)
                curveTo(41.24f, 137.8f, 38.74f, 139.48f, 37.54f, 141.91f)
                lineTo(40.48f, 143.37f)
                curveTo(41.15f, 142.0f, 42.54f, 141.08f, 44.15f, 141.08f)
                curveTo(46.4f, 141.08f, 48.24f, 142.92f, 48.24f, 145.2f)
                horizontalLineTo(51.52f)
                curveTo(51.52f, 141.12f, 48.23f, 137.8f, 44.15f, 137.8f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = null,
                fillAlpha = 0.8f,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(48.24f, 150.33f)
                verticalLineTo(147.76f)
                horizontalLineTo(51.52f)
                verticalLineTo(150.33f)
                curveTo(51.52f, 154.41f, 48.23f, 157.73f, 44.15f, 157.73f)
                verticalLineTo(154.45f)
                curveTo(46.4f, 154.45f, 48.24f, 152.61f, 48.24f, 150.33f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)),
                stroke = null,
                fillAlpha = 0.8f,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(40.05f, 150.33f)
                verticalLineTo(145.14f)
                horizontalLineTo(36.77f)
                verticalLineTo(150.33f)
                curveTo(36.77f, 153.23f, 38.44f, 155.75f, 40.87f, 156.96f)
                lineTo(42.33f, 154.03f)
                curveTo(40.98f, 153.35f, 40.05f, 151.95f, 40.05f, 150.33f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFFffffff)),
                strokeLineWidth = 3.0f,
                strokeLineCap = Round,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(90.25f, 129.6f)
                lineTo(210.74f, 130.46f)
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFFffffff)),
                strokeLineWidth = 3.0f,
                strokeLineCap = Round,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(90.25f, 150.06f)
                lineTo(286.94f, 150.06f)
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFFffffff)),
                strokeLineWidth = 3.0f,
                strokeLineCap = Round,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(90.25f, 169.78f)
                lineTo(286.94f, 169.78f)
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
                moveTo(184.28f, 191.3f)
                lineTo(180.65f, 184.15f)
                lineTo(176.99f, 186.01f)
                lineTo(180.62f, 193.16f)
                lineTo(172.52f, 194.02f)
                lineTo(173.03f, 198.0f)
                lineTo(181.04f, 196.95f)
                lineTo(179.82f, 204.97f)
                lineTo(184.04f, 205.4f)
                lineTo(185.04f, 197.26f)
                lineTo(192.57f, 201.06f)
                lineTo(194.26f, 197.56f)
                lineTo(187.16f, 193.84f)
                lineTo(192.85f, 187.93f)
                lineTo(189.76f, 185.27f)
                lineTo(184.28f, 191.3f)
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
                moveTo(241.83f, 77.43f)
                lineTo(243.16f, 71.15f)
                lineTo(239.96f, 70.47f)
                lineTo(238.63f, 76.76f)
                lineTo(233.2f, 73.23f)
                lineTo(231.52f, 75.97f)
                lineTo(236.99f, 79.33f)
                lineTo(232.23f, 83.73f)
                lineTo(234.63f, 86.12f)
                lineTo(239.32f, 81.54f)
                lineTo(242.07f, 87.69f)
                lineTo(244.87f, 86.35f)
                lineTo(242.34f, 80.47f)
                lineTo(248.81f, 79.63f)
                lineTo(248.23f, 76.42f)
                lineTo(241.83f, 77.43f)
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
                moveTo(277.6f, 102.56f)
                verticalLineTo(93.22f)
                horizontalLineTo(272.81f)
                verticalLineTo(102.56f)
                lineTo(263.94f, 99.18f)
                lineTo(262.37f, 103.58f)
                lineTo(271.24f, 106.72f)
                lineTo(265.75f, 114.41f)
                lineTo(269.91f, 117.08f)
                lineTo(275.24f, 109.15f)
                lineTo(281.05f, 117.08f)
                lineTo(284.66f, 114.33f)
                lineTo(279.25f, 106.72f)
                lineTo(288.27f, 103.58f)
                lineTo(286.47f, 99.18f)
                lineTo(277.6f, 102.56f)
                close()
            }
        }.build()
        return _bannerNotificationLight!!
    }

private var _bannerNotificationLight: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box {
        Image(
            imageVector = AppIllus.BannerNotificationLight,
            contentDescription = null,
            modifier = Modifier.size(AppImages.previewSize),
        )
    }
}
