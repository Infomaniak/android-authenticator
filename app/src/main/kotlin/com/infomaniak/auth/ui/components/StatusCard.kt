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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewLightAndDark

@Immutable
sealed interface CardVariant {
    data object Neutral : CardVariant {
        @Composable
        override fun getMaterialTheme(): ColorScheme = MaterialTheme.colorScheme.copy(
            surface = MaterialTheme.colorScheme.surfaceContainer,
            outlineVariant = MaterialTheme.colorScheme.outline
        )
    }

    data object Warning : CardVariant {
        @Composable
        override fun getMaterialTheme(): ColorScheme = MaterialTheme.colorScheme.copy(
            primary = AuthenticatorTheme.statusColors.warningHighest,
            secondary = AuthenticatorTheme.statusColors.warningMedium,
            onSecondary = AuthenticatorTheme.statusColors.warningHighest,
            surface = AuthenticatorTheme.statusColors.warningLow,
            onSurface = AuthenticatorTheme.statusColors.warningHighest,
            outlineVariant = AuthenticatorTheme.statusColors.warningHigh,
        )
    }

    data object Error : CardVariant {
        @Composable
        override fun getMaterialTheme(): ColorScheme = MaterialTheme.colorScheme.copy(
            primary = AuthenticatorTheme.statusColors.errorHighest,
            secondary = AuthenticatorTheme.statusColors.errorMedium,
            onSecondary = AuthenticatorTheme.statusColors.errorHighest,
            surface = AuthenticatorTheme.statusColors.errorLow,
            onSurface = AuthenticatorTheme.statusColors.errorHighest,
            outlineVariant = AuthenticatorTheme.statusColors.errorHigh,
        )
    }

    @Composable
    fun getMaterialTheme(): ColorScheme
}

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Neutral,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    MaterialTheme(colorScheme = variant.getMaterialTheme()) {
        OutlinedCard(
            modifier = modifier,
            shape = shape,
            content = content
        )
    }
}

@PreviewLightAndDark
@Composable
private fun StatusCardPreview() {
    AuthenticatorTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Margin.Medium),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val themeCardModifier = Modifier.fillMaxWidth()

                StatusCard(modifier = themeCardModifier, variant = CardVariant.Neutral) {
                    PreviewContent()
                }
                StatusCard(modifier = themeCardModifier, variant = CardVariant.Error) {
                    PreviewContent()
                }
                StatusCard(modifier = themeCardModifier, variant = CardVariant.Warning) {
                    PreviewContent()
                }
            }
        }
    }
}

@Composable
private fun PreviewContent() {
    Row(modifier = Modifier.padding(Margin.Medium), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.triangle_alert),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = Margin.Small),
            text = stringResource(R.string.accountNotConnectedWarningTitle),
        )

    }
    LargeButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .padding(bottom = Margin.Medium),
        style = ButtonStyle.Primary,
        title = stringResource(R.string.logInButton),
        onClick = {}
    )
    LargeButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .padding(bottom = Margin.Medium),
        style = ButtonStyle.Secondary,
        title = stringResource(R.string.logInButton),
        onClick = {}
    )
    LargeButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .padding(bottom = Margin.Medium),
        title = stringResource(R.string.appName),
        style = ButtonStyle.Tertiary,
        onClick = {}
    )
}
