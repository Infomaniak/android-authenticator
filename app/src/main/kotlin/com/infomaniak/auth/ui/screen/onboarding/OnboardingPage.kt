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

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.images.AppImages.AppIllus
import com.infomaniak.auth.ui.images.illus.blueBlur.BlueBlur
import com.infomaniak.auth.ui.images.illus.shieldPerson.ShieldPerson
import com.infomaniak.core.onboarding.OnboardingPage
import com.infomaniak.core.onboarding.components.OnboardingComponents.DefaultLottieIllustration
import com.infomaniak.core.onboarding.components.OnboardingComponents.DefaultTitleAndDescription
import com.infomaniak.core.ui.compose.theme.ThemedImage

internal enum class Page(
    val background: IllustrationResource,
    val illustration: IllustrationResource,
    @field:StringRes val titleRes: Int,
    @field:StringRes val descriptionRes: Int,
) {
    LOGIN(
        background = IllustrationResource.Vector(AppIllus.BlueBlur),
        illustration = IllustrationResource.Vector(AppIllus.ShieldPerson),
        titleRes = R.string.onBoardingLoginTitle,
        descriptionRes = R.string.onBoardingLoginDescription
    );
}

@Composable
internal fun Page.toOnboardingPage(pagerState: PagerState, index: Int) = OnboardingPage(
    background = { },
    illustration = { OnboardingPageIllustration(pagerState, index) },
    text = {
        DefaultTitleAndDescription(
            title = stringResource(titleRes),
            description = stringResource(descriptionRes),
            titleStyle = MaterialTheme.typography.headlineMedium,
            descriptionStyle = MaterialTheme.typography.bodyLarge
        )
    }
)

@Composable
private fun Page.OnboardingPageIllustration(pagerState: PagerState, index: Int) {
    Box {
        RenderIllustration(background, pagerState, index)
        RenderIllustration(illustration, pagerState, index)
    }
}

internal sealed class IllustrationResource {
    data class Static(@field:DrawableRes val res: Int) : IllustrationResource()
    data class Vector(val image: ThemedImage) : IllustrationResource()
    data class Animated(@field:RawRes val res: Int) : IllustrationResource()
}

@Composable
private fun RenderIllustration(resource: IllustrationResource, pagerState: PagerState, index: Int) {
    when (resource) {
        is IllustrationResource.Static -> {
            Image(
                modifier = Modifier.size(350.dp),
                painter = painterResource(id = resource.res),
                contentDescription = null,
            )
        }
        is IllustrationResource.Vector -> {
            Image(
                modifier = Modifier.size(350.dp),
                imageVector = resource.image.image(),
                contentDescription = null,
            )
        }
        is IllustrationResource.Animated -> {
            DefaultLottieIllustration(
                lottieRawRes = resource.res,
                isCurrentPageVisible = { pagerState.currentPage == index }
            )
        }
    }
}
