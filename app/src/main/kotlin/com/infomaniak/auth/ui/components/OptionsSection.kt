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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun OptionsSection(
    vararg sections: List<OptionItemType>,
    modifier: Modifier = Modifier,
) {
    OptionsSectionContainer(modifier, *sections) { optionItems ->
        optionItems.forEachIndexed { index, optionItemType ->
            OptionItem(optionItemType = optionItemType)

            if (index < optionItems.lastIndex) {
                HorizontalDivider(
                    color = AuthenticatorTheme.materialColors.outlineVariant,
                )
            }
        }
    }
}

@Composable
private fun OptionsSectionContainer(
    modifier: Modifier = Modifier,
    vararg sections: List<OptionItemType>,
    content: @Composable (optionItem: List<OptionItemType>) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = Margin.Large),
        verticalArrangement = Arrangement.spacedBy(Margin.Large)
    ) {
        sections.forEach { optionsSection ->
            if (optionsSection.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Margin.Medium),
                    colors = CardDefaults.cardColors(containerColor = AuthenticatorTheme.customColors.sectionBackground),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Column {
                        content(optionsSection)
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionItem(optionItemType: OptionItemType) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .clickable(enabled = optionItemType is OptionItemType.WithRightIcon || optionItemType is OptionItemType.WithSelection) {
                optionItemType.onClick?.invoke()
            }
    ) {
        OptionContent(optionItemType)
    }
}

@Composable
private fun OptionContent(optionItemType: OptionItemType) {
    Row(
        modifier = Modifier
            .padding(horizontal = Margin.Medium)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(optionItemType.stringResId), color = optionItemType.textColor)
        Spacer(modifier = Modifier.weight(1f))

        when (optionItemType) {
            is OptionItemType.WithRightIcon -> {
                Icon(
                    painter = painterResource(optionItemType.rightIconResId),
                    contentDescription = null,
                )
            }
            is OptionItemType.WithCheckBox -> {
                Switch(
                    modifier = Modifier
                        .scale(0.7f),
                    checked = optionItemType.isChecked,
                    onCheckedChange = { optionItemType.onCheckedChange.invoke(it) },
                )
            }
            is OptionItemType.WithSelection -> {
                if (optionItemType.isSelected) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.check),
                        contentDescription = null,
                        tint = AuthenticatorTheme.materialColors.primary,
                    )
                }
            }
            else -> {}
        }
    }
}

sealed class OptionItemType(
    val stringResId: Int,
    val textColor: Color = Color.Unspecified,
    val onClick: (() -> Unit)? = null,
) {

    class WithCheckBox(
        stringResId: Int,
        val isChecked: Boolean,
        val onCheckedChange: ((isChecked: Boolean) -> Unit)
    ) : OptionItemType(stringResId)

    class WithRightIcon(
        stringResId: Int,
        val rightIconResId: Int,
        onClick: () -> Unit,
    ) : OptionItemType(stringResId, onClick = onClick)

    class WithSelection(
        stringResId: Int,
        val isSelected: Boolean,
        onClick: () -> Unit,
    ) : OptionItemType(stringResId, onClick = onClick)

    class Default(stringResId: Int, textColor: Color = Color.Unspecified) : OptionItemType(stringResId, textColor)
}

@PreviewSmallWindow
@Composable
fun OptionsSectionPreview() {
    val firstSectionItems = listOf(
        OptionItemType.WithCheckBox(stringResId = R.string.appCompleteName, isChecked = false, onCheckedChange = {}),
        OptionItemType.WithRightIcon(
            stringResId = R.string.appCompleteName,
            rightIconResId = R.drawable.right_indicator,
            onClick = {}),
    )

    val secondSectionItems =
        listOf(OptionItemType.WithCheckBox(stringResId = R.string.appCompleteName, isChecked = false, onCheckedChange = {}))

    AuthenticatorTheme {
        Column(modifier = Modifier.background(AuthenticatorTheme.materialColors.inverseOnSurface)) {
            OptionsSection(
                firstSectionItems, secondSectionItems,
                modifier = Modifier.padding(PaddingValues(Margin.Small)),
            )
        }
    }
}
