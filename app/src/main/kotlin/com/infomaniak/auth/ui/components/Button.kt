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

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.theme.AppDimens
import com.infomaniak.auth.ui.theme.AppShapes
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.basicbutton.BasicButton
import com.infomaniak.core.ui.compose.basics.Dimens
import com.infomaniak.core.ui.compose.basics.Typography
import com.infomaniak.core.ui.compose.margin.Margin

@Composable
fun LargeButton(
    title: String,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    enabled: () -> Boolean = { true },
    showIndeterminateProgress: () -> Boolean = { false },
    progress: (() -> Float)? = null,
    onClick: () -> Unit,
    imageVector: ImageVector? = null,
) {
    BasicButton(
        onClick = onClick,
        modifier = modifier.height(AppDimens.LargeButtonHeight),
        shape = AppShapes.LargeButtonShape,
        colors = style.colors(),
        enabled = enabled,
        showIndeterminateProgress = showIndeterminateProgress,
        progress = progress,
    ) {
        imageVector?.let {
            Icon(modifier = Modifier.size(Dimens.smallIconSize), imageVector = it, contentDescription = null)
            Spacer(Modifier.width(Margin.Mini))
        }
        Text(text = title, style = Typography.bodyMedium)
    }
}

enum class ButtonStyle(val colors: @Composable () -> ButtonColors) {
    Primary({
        ButtonDefaults.buttonColors(
            containerColor = AuthenticatorTheme.materialColors.primary,
            contentColor = AuthenticatorTheme.materialColors.onPrimary,
        )
    }),
    Secondary({
        ButtonDefaults.buttonColors(
            containerColor = AuthenticatorTheme.materialColors.secondary,
            contentColor = AuthenticatorTheme.materialColors.onSecondary,
        )
    }),
    Tertiary({
        ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = AuthenticatorTheme.materialColors.primary,
            disabledContainerColor = Color.Transparent,
        )
    }),
}

@Preview(name = "Light", widthDp = 800)
@Preview(name = "Dark", widthDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun LargeButtonPreview() {
    AuthenticatorTheme {
        Surface {
            Column {
                ButtonStyle.entries.forEach {
                    Row {
                        LargeButton(
                            title = stringResource(R.string.appName),
                            style = it,
                            onClick = {}
                        )
                        Spacer(Modifier.width(Margin.Mini))
                        LargeButton(
                            title = stringResource(R.string.appName),
                            style = it,
                            progress = { 0.3f },
                            onClick = {},
                        )

                        Spacer(Modifier.width(Margin.Mini))
                        LargeButton(
                            modifier = Modifier.width(250.dp),
                            title = stringResource(R.string.appName),
                            style = it,
                            onClick = {}
                        )
                    }
                    Spacer(Modifier.height(Margin.Medium))
                }
            }
        }
    }
}
