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
package com.infomaniak.auth.ui.screen.onboarding

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.images.AppImages.AppIllus
import com.infomaniak.auth.ui.images.illus.shieldPerson.ShieldPerson
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.onboarding.OnboardingPage
import com.infomaniak.core.onboarding.OnboardingScaffold
import com.infomaniak.core.onboarding.components.OnboardingComponents.DefaultTitleAndDescription
import com.infomaniak.core.onboarding.components.OnboardingComponents.ThemedDotLottie
import com.infomaniak.core.onboarding.models.OnboardingLottieSource
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import com.infomaniak.core.ui.compose.theme.LocalIsThemeDarkMode
import com.infomaniak.core.ui.compose.theme.ThemedImage

internal enum class Page(
    val illustration: IllustrationResource,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
) {
    Login(
        illustration = IllustrationResource.Vector(AppIllus.ShieldPerson),
        titleRes = R.string.onBoardingLoginTitle,
        descriptionRes = R.string.onBoardingLoginDescription
    );
}

@Composable
internal fun Page.toOnboardingPage(pagerState: PagerState, index: Int) = OnboardingPage(
    background = {},
    illustration = { OnboardingPageIllustration(pagerState, index) },
    text = {
        DefaultTitleAndDescription(
            title = stringResource(titleRes),
            description = stringResource(descriptionRes),
            titleStyle = MaterialTheme.typography.titleLarge,
            descriptionStyle = MaterialTheme.typography.bodyLarge
        )
    }
)

@Composable
private fun Page.OnboardingPageIllustration(pagerState: PagerState, index: Int) {
    Box {
        val isCurrentPageVisible = { pagerState.currentPage == index }
        RenderIllustration(illustration, isCurrentPageVisible)
    }
}

@Composable
private fun RenderIllustration(resource: IllustrationResource, isCurrentPageVisible: () -> Boolean) {
    when (resource) {
        is IllustrationResource.Vector -> {
            IllustrationWithHalo(resource.themedImage)
        }
        is IllustrationResource.Animated -> {
            ThemedDotLottie(
                source = OnboardingLottieSource.Res(resource.res),
                isCurrentPageVisible = isCurrentPageVisible,
                themeId = { if (LocalIsThemeDarkMode.current) resource.themeIdDark else resource.themeIdLight }
            )
        }
    }
}

internal sealed class IllustrationResource {
    data class Vector(val themedImage: ThemedImage) : IllustrationResource()
    data class Animated(
        @RawRes val res: Int,
        val themeIdLight: String? = null,
        val themeIdDark: String? = null,
    ) : IllustrationResource()
}

@PreviewSmallWindow
@Composable
private fun OnboardingPagePreview() {
    val pagerState = rememberPagerState(pageCount = { 1 })
    val page = Page.Login.toOnboardingPage(pagerState = pagerState, index = 0)

    AuthenticatorTheme {
        OnboardingScaffold(
            pagerState = pagerState,
            onboardingPages = listOf(page),
            bottomContent = {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray)
                ) {
                    Text("Bottom content")
                }
            },
        )
    }
}
