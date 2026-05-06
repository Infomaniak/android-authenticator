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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.ui.theme.AppShapes.MediumButtonShape
import com.infomaniak.core.common.extensions.openUrlInCustomTab
import com.infomaniak.core.ui.compose.margin.Margin

@Composable
fun OpenUrlButton(
    text: String,
    sourceUrl: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val context = LocalContext.current

    TextButton(
        modifier = modifier.heightIn(min = 48.dp),
        onClick = { context.openUrlInCustomTab(sourceUrl) },
        contentPadding = PaddingValues(horizontal = Margin.Medium),
        shape = MediumButtonShape,
    ) {
        leadingIcon?.invoke()
        Text(text = text)
    }
}
