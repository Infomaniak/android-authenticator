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
package com.infomaniak.auth.ui.screen.accountdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.Issue
import com.infomaniak.auth.lib.models.UrlConstants.HELP_SUPPORT_URL
import com.infomaniak.auth.ui.components.ButtonStyle
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.components.StatusCard
import com.infomaniak.auth.ui.components.StatusCardVariant
import com.infomaniak.auth.ui.theme.AppDimens.DefaultCornerRadius
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.common.extensions.openUrl
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.common.R as RCore

private data class ActionRequiredConfiguration(
    val text: String,
    val iconColor: Color,
    val iconRes: Int,
    val statusCardVariant: StatusCardVariant
)

@Composable
fun ActionRequiredCard(
    status: Account.Status,
    onLoginPressed: (Long) -> Unit,
) {
    val context = LocalContext.current

    when (status) {
        is Account.Status.NotConnected.ReLogin -> {
            ActionRequiredCard(
                configuration = ActionRequiredConfiguration(
                    text = stringResource(R.string.accountNotConnectedWarningTitle),
                    iconRes = R.drawable.alert,
                    iconColor = AuthenticatorTheme.customColors.iconTintWarning,
                    statusCardVariant = StatusCardVariant.Warning
                ),
                bottomButton = {
                    ActionRequiredButton(
                        title = stringResource(R.string.logInButton),
                        style = ButtonStyle.Primary,
                        onClick = {
                            val legacyAccount = status.legacyAccount
                            onLoginPressed(legacyAccount.id)
                        }
                    )
                }
            )
        }
        is Account.Status.NotConnected.LoginFailed -> {
            if (status.cause is Issue.Retriable) {
                ActionRequiredCard(
                    configuration = ActionRequiredConfiguration(
                        text = stringResource(R.string.errorMigrationFailed),
                        iconRes = R.drawable.alert,
                        iconColor = AuthenticatorTheme.customColors.iconTintWarning,
                        statusCardVariant = StatusCardVariant.Warning
                    ),
                    bottomButton = {
                        ActionRequiredButton(
                            title = stringResource(R.string.contactSupportTitle),
                            style = ButtonStyle.Primary,
                            onClick = {
                                context.openUrl(HELP_SUPPORT_URL)
                            }
                        )
                        ActionRequiredButton(
                            title = stringResource(RCore.string.buttonRetry),
                            style = ButtonStyle.Tertiary,
                            onClick = {
                                val cause = status.cause as? Issue.Retriable
                                cause?.proceed?.invoke(true)
                            }
                        )
                    }
                )
            } else {
                ActionRequiredCard(
                    configuration = ActionRequiredConfiguration(
                        text = stringResource(R.string.errorLoginFailed, 0),
                        iconRes = R.drawable.triangle_alert,
                        iconColor = AuthenticatorTheme.customColors.iconTintWarning,
                        statusCardVariant = StatusCardVariant.Warning
                    ),
                    bottomButton = {
                        ActionRequiredButton(
                            title = stringResource(R.string.contactSupportTitle),
                            style = ButtonStyle.Primary,
                            onClick = { context.openUrl(HELP_SUPPORT_URL) }
                        )
                    }
                )
            }
        }
        else -> Unit
    }
}

@Composable
private fun ActionRequiredCard(
    configuration: ActionRequiredConfiguration,
    bottomButton: @Composable ColumnScope.() -> Unit,
) {
    StatusCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .padding(top = Margin.Large),
        shape = RoundedCornerShape(DefaultCornerRadius),
        variant = configuration.statusCardVariant,
    ) {
        Row(modifier = Modifier.padding(Margin.Medium), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(configuration.iconRes),
                contentDescription = null,
                tint = configuration.iconColor
            )
            Text(
                modifier = Modifier.padding(start = Margin.Small),
                text = configuration.text,
            )
        }
        Column(content = bottomButton)
    }
}

@Composable
private fun ActionRequiredButton(
    title: String,
    style: ButtonStyle,
    onClick: () -> Unit
) {
    LargeButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Margin.Medium)
            .padding(bottom = Margin.Medium),
        title = title,
        style = style,
        onClick = onClick
    )
}
