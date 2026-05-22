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

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.models.UrlConstants
import com.infomaniak.auth.lib.models.UrlConstants.SETTINGS_ACCOUNT_SECURITY_URL
import com.infomaniak.auth.ui.components.ButtonStyle
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.theme.AppDimens.DefaultCornerRadius
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.common.extensions.openUrlInCustomTab
import com.infomaniak.core.network.ApiEnvironment
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewLightAndDark
import com.infomaniak.core.webview.ui.WebViewActivity

@Composable
fun AccountSecurityCard(
    configuration: AccountSecurityConfiguration,
    modifier: Modifier = Modifier,
    token: String? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Margin.Large)
            .padding(horizontal = Margin.Medium),
        shape = RoundedCornerShape(DefaultCornerRadius),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = AuthenticatorTheme.customColors.sectionBackground)
    ) {
        Column(
            modifier = Modifier.padding(Margin.Medium),
            verticalArrangement = Arrangement.spacedBy(Margin.Medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(configuration.titleResId),
                    style = if (configuration.descriptionResId != null) MaterialTheme.typography.titleLarge else LocalTextStyle.current
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(configuration.iconResId),
                    contentDescription = null,
                    tint = configuration.iconTint?.invoke() ?: AuthenticatorTheme.customColors.iconTintWarning,
                )
            }
            configuration.descriptionResId?.let { descriptionResId ->
                Text(text = stringResource(descriptionResId))
            }
            if (configuration.action != null) {
                val context = LocalContext.current
                LargeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Margin.Medium)
                        .padding(bottom = Margin.Medium),
                    title = stringResource(R.string.updateButton),
                    style = ButtonStyle.Primary,
                    onClick = { configuration.action(context, token) }
                )
            }
        }
    }
}

enum class AccountSecurityConfiguration(
    val titleResId: Int,
    val descriptionResId: Int? = null,
    val iconResId: Int,
    val iconTint: @Composable (() -> Color)? = null,
    val action: ((Context, String?) -> Unit)? = null,
) {
    Secured(
        titleResId = R.string.accountProtected,
        iconResId = R.drawable.shield_check,
        iconTint = { AuthenticatorTheme.customColors.iconTintSuccess }
    ),
    PartiallyProtected(
        titleResId = R.string.accountPartiallyProtectedTitle,
        descriptionResId = R.string.accountPartiallyProtectedDescription,
        iconResId = R.drawable.shield_exclamation_mark,
        iconTint = { AuthenticatorTheme.customColors.iconTintWarning },
        action = { context, token ->
            val host = ApiEnvironment.current.host
            val url = UrlConstants.autologUrl(host, UrlConstants.managerUrl(host = host, SETTINGS_ACCOUNT_SECURITY_URL))

            WebViewActivity.startActivity(
                context = context,
                url = url,
                headers = token?.let { mapOf("Authorization" to "Bearer $token") } ?: emptyMap(),
            )
        }
    ),
    Disconnected(
        titleResId = R.string.disconnectSuccess,
        iconResId = R.drawable.circle_block,
        iconTint = { AuthenticatorTheme.customColors.iconTintDisconnected }
    );

    companion object {
        fun Account.Status.toSecurityConfiguration(): AccountSecurityConfiguration = when (this) {
            is Account.Status.LoggedIn if !isSecured -> PartiallyProtected
            is Account.Status.LoggedIn -> Secured
            is Account.Status.NotConnected -> Disconnected
        }
    }
}

@PreviewLightAndDark
@Composable
private fun AccountSecurityCardPreview() {
    AuthenticatorTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Margin.Medium),
                verticalArrangement = Arrangement.spacedBy(Margin.Medium)
            ) {
                AccountSecurityCard(configuration = AccountSecurityConfiguration.Secured)
                AccountSecurityCard(configuration = AccountSecurityConfiguration.PartiallyProtected)
                AccountSecurityCard(configuration = AccountSecurityConfiguration.Disconnected)
            }
        }
    }
}
