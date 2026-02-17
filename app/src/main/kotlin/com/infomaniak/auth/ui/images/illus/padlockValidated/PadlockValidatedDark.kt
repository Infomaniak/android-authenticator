package com.infomaniak.auth.ui.images.illus.padlockValidated

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

val AppIllus.PadlockValidatedDark: ImageVector
    get() {
        if (_padlockValidatedDark != null) {
            return _padlockValidatedDark!!
        }
        _padlockValidatedDark = Builder(
            name = "PadlockValidatedDark",
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
                fill = SolidColor(Color(0xFF5869D9)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(244.4f, 233.1f)
                curveTo(244.4f, 249.32f, 231.25f, 262.48f, 215.03f, 262.48f)
                curveTo(198.81f, 262.48f, 185.65f, 249.32f, 185.65f, 233.1f)
                curveTo(185.65f, 216.88f, 198.81f, 203.73f, 215.03f, 203.73f)
                curveTo(231.25f, 203.73f, 244.4f, 216.88f, 244.4f, 233.1f)
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
                moveTo(215.03f, 215.48f)
                curveTo(219.67f, 215.43f, 224.26f, 216.37f, 228.51f, 218.23f)
                lineTo(228.53f, 218.24f)
                curveTo(229.11f, 218.5f, 229.6f, 218.92f, 229.94f, 219.46f)
                curveTo(230.28f, 219.99f, 230.45f, 220.61f, 230.45f, 221.24f)
                verticalLineTo(232.42f)
                curveTo(230.45f, 236.18f, 229.31f, 239.85f, 227.18f, 242.94f)
                curveTo(225.06f, 246.04f, 222.04f, 248.42f, 218.54f, 249.77f)
                lineTo(217.01f, 250.36f)
                curveTo(216.37f, 250.6f, 215.7f, 250.73f, 215.03f, 250.73f)
                curveTo(214.36f, 250.73f, 213.69f, 250.6f, 213.05f, 250.36f)
                lineTo(211.52f, 249.77f)
                curveTo(208.02f, 248.42f, 205.0f, 246.04f, 202.87f, 242.94f)
                curveTo(200.75f, 239.85f, 199.61f, 236.18f, 199.61f, 232.42f)
                verticalLineTo(221.24f)
                curveTo(199.6f, 220.61f, 199.78f, 219.99f, 200.12f, 219.46f)
                curveTo(200.46f, 218.92f, 200.95f, 218.5f, 201.52f, 218.24f)
                lineTo(201.54f, 218.23f)
                curveTo(205.79f, 216.37f, 210.39f, 215.43f, 215.03f, 215.48f)
                close()
                moveTo(215.02f, 217.68f)
                curveTo(210.69f, 217.64f, 206.4f, 218.51f, 202.43f, 220.24f)
                curveTo(202.25f, 220.33f, 202.09f, 220.47f, 201.98f, 220.64f)
                curveTo(201.87f, 220.82f, 201.81f, 221.02f, 201.81f, 221.23f)
                verticalLineTo(232.42f)
                curveTo(201.81f, 235.73f, 202.81f, 238.97f, 204.69f, 241.7f)
                curveTo(206.56f, 244.43f, 209.22f, 246.53f, 212.31f, 247.71f)
                lineTo(213.84f, 248.3f)
                curveTo(214.22f, 248.45f, 214.63f, 248.52f, 215.03f, 248.52f)
                curveTo(215.43f, 248.52f, 215.83f, 248.45f, 216.22f, 248.3f)
                lineTo(217.74f, 247.71f)
                curveTo(220.84f, 246.53f, 223.49f, 244.43f, 225.37f, 241.7f)
                curveTo(227.24f, 238.97f, 228.25f, 235.73f, 228.25f, 232.42f)
                verticalLineTo(221.23f)
                curveTo(228.25f, 221.02f, 228.19f, 220.82f, 228.08f, 220.64f)
                curveTo(227.97f, 220.47f, 227.81f, 220.33f, 227.62f, 220.24f)
                curveTo(223.66f, 218.51f, 219.37f, 217.64f, 215.04f, 217.68f)
                lineTo(215.02f, 217.68f)
                close()
                moveTo(221.86f, 225.46f)
                curveTo(222.23f, 224.98f, 222.92f, 224.88f, 223.4f, 225.24f)
                curveTo(223.89f, 225.61f, 223.99f, 226.3f, 223.62f, 226.79f)
                lineTo(213.55f, 240.17f)
                curveTo(213.32f, 240.49f, 213.01f, 240.74f, 212.66f, 240.92f)
                curveTo(212.31f, 241.1f, 211.91f, 241.19f, 211.51f, 241.18f)
                curveTo(211.11f, 241.16f, 210.73f, 241.05f, 210.38f, 240.85f)
                curveTo(210.04f, 240.65f, 209.76f, 240.38f, 209.55f, 240.05f)
                lineTo(206.4f, 235.37f)
                curveTo(206.07f, 234.86f, 206.2f, 234.18f, 206.71f, 233.84f)
                curveTo(207.21f, 233.5f, 207.89f, 233.63f, 208.23f, 234.14f)
                lineTo(211.38f, 238.83f)
                lineTo(211.4f, 238.85f)
                curveTo(211.42f, 238.9f, 211.46f, 238.93f, 211.49f, 238.95f)
                curveTo(211.52f, 238.96f, 211.55f, 238.97f, 211.58f, 238.97f)
                curveTo(211.61f, 238.98f, 211.64f, 238.97f, 211.68f, 238.95f)
                curveTo(211.71f, 238.94f, 211.74f, 238.91f, 211.77f, 238.87f)
                lineTo(211.78f, 238.85f)
                lineTo(221.86f, 225.46f)
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
        return _padlockValidatedDark!!
    }

private var _padlockValidatedDark: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box {
        Image(
            imageVector = AppIllus.PadlockValidatedDark,
            contentDescription = null,
            modifier = Modifier.size(AppImages.previewSize),
        )
    }
}
