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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.images.AppImages.AppIllus
import com.infomaniak.auth.ui.images.illus.blueBlur.BlueBlur
import com.infomaniak.auth.ui.images.illus.shieldPerson.ShieldPerson
import com.infomaniak.core.onboarding.OnboardingPage
import com.infomaniak.core.onboarding.components.OnboardingComponents.DefaultTitleAndDescription
import com.infomaniak.core.onboarding.components.OnboardingComponents.ThemedDotLottie
import com.infomaniak.core.onboarding.models.OnboardingLottieSource
import com.infomaniak.core.ui.compose.theme.LocalIsThemeDarkMode
import com.infomaniak.core.ui.compose.theme.ThemedImage

internal enum class Page(
    val background: IllustrationResource,
    val illustration: IllustrationResource,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
) {
    Login(
        background = IllustrationResource.Vector(AppIllus.BlueBlur),
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
        RenderIllustration(background, isCurrentPageVisible)
        RenderIllustration(illustration, isCurrentPageVisible)
    }
}

@Composable
private fun RenderIllustration(resource: IllustrationResource, isCurrentPageVisible: () -> Boolean) {
    when (resource) {
        is IllustrationResource.Vector -> {
            Image(
                modifier = Modifier.size(350.dp),
                imageVector = resource.themedImage.image(),
                contentDescription = null,
            )
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
