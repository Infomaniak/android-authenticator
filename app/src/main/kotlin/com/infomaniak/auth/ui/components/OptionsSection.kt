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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.margin.Margin


@Composable
fun OptionsSection(vararg optionsSections: List<OptionItem>) {
    optionsSections.forEach { optionsSection ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Margin.Medium),
            colors = CardDefaults.cardColors(containerColor = AuthenticatorTheme.colors.optionsSectionBackground),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column {
                optionsSection.forEach { optionItem ->
                    OptionItem(optionItem)
                }
            }
        }
    }
}

@Composable
private fun OptionItem(optionItem: OptionItem) {
    if (optionItem.dividerState.withUpperDivider) {
        HorizontalDivider(
            color = AuthenticatorTheme.materialColors.inverseOnSurface,
        )
    }
    Box(
        modifier = Modifier
            .height(50.dp)
            .then(if (optionItem is OptionItem.WithRightIcon) Modifier.clickable(onClick = {}) else Modifier),
    ) {
        OptionContent(optionItem)
    }
    if (optionItem.dividerState.withLowerDivider) {
        HorizontalDivider(
            color = AuthenticatorTheme.materialColors.inverseOnSurface,
        )
    }
}

@Composable
private fun OptionContent(optionItem: OptionItem) {
    Row(
        modifier = Modifier
            .padding(horizontal = Margin.Medium)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(optionItem.stringResId))
        Spacer(modifier = Modifier.weight(1f))
        if (optionItem is OptionItem.WithRightIcon) {
            Icon(
                painter = painterResource(optionItem.rightIconResId),
                contentDescription = null,
            )
        } else {
            Switch(
                modifier = Modifier
                    .scale(0.7f),
                checked = true,
                onCheckedChange = { },
            )
        }
    }
}

sealed class OptionItem(val stringResId: Int, val dividerState: DividerState) {
    class WithCheckBox(stringResId: Int, dividerState: DividerState) : OptionItem(stringResId, dividerState)
    class WithRightIcon(stringResId: Int, dividerState: DividerState, val rightIconResId: Int) :
        OptionItem(stringResId, dividerState)
}

data class DividerState(val withUpperDivider: Boolean, val withLowerDivider: Boolean)
