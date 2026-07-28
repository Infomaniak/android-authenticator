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
package com.infomaniak.auth.ui.screen.webview

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.core.webview.ui.components.WebView
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@SuppressLint("ComposeViewModelInjection")
@Composable
fun WebviewScreen(
    url: String,
    headers: ImmutableMap<String, String>?,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    refreshProfileOnClose: Boolean = false,
) {
    if (refreshProfileOnClose) {
        val viewModel: WebviewScreenViewModel = hiltViewModel()

        DisposableEffect(Unit) {
            onDispose {
                viewModel.refreshUserProfiles()
            }
        }
    }

    @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        modifier = modifier,
        topBar = {
            InfomaniakAuthenticatorTopAppBar(
                titleResId = R.string.webview_domain,
                isCentered = false,
                onBackPressed = onBackPressed
            )
        }
    ) { _ -> // Avoid redundant padding (our Webview is already using safeDrawingPadding)
        WebView(
            url = url,
            headers = headers ?: persistentMapOf(),
            onUrlToQuitReached = onBackPressed,
            urlToQuit = null,
            domStorageEnabled = true,
        )
    }
}
