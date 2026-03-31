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
package com.infomaniak.auth.ui.screen.onboarding.migration

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.ui.components.EmptyElement
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.components.TitleAndDescription
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.gridTilesWithAuthenticator.GridTilesWithAuthenticator
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.avatar.LocalAvatarColors
import com.infomaniak.core.avatar.components.Avatar
import com.infomaniak.core.avatar.getBackgroundColorResBasedOnId
import com.infomaniak.core.avatar.models.AvatarColors
import com.infomaniak.core.avatar.models.AvatarType
import com.infomaniak.core.avatar.models.AvatarUrlData
import com.infomaniak.core.coil.ImageLoaderProvider
import com.infomaniak.core.crossapplogin.front.views.components.smallProgressStrokeWidth
import com.infomaniak.core.ui.compose.basics.Dimens
import com.infomaniak.core.ui.compose.basics.Typography
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.BottomStickyButtonScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun MigrationScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: MigrationViewModel = hiltViewModel(),
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle(emptyList())
    MigrationScreen(
        accounts = { accounts },
        onContinue = {}
    )
}

@Composable
fun MigrationScreen(
    accounts: () -> List<Account>,
    modifier: Modifier = Modifier,
    onContinue: () -> Unit,
) {
    BottomStickyButtonScaffold(
        modifier = modifier,
        topBar = {
            InfomaniakAuthenticatorTopAppBar()
        },
        bottomButton = { bottomModifier ->
            Column(
                modifier = bottomModifier,
                verticalArrangement = Arrangement.spacedBy(Margin.Medium)
            ) {
                MigrationSelectAccounts(
                    accounts = accounts,
                    isLoading = { false },
                )
                LargeButton(modifier = bottomModifier, title = stringResource(R.string.continueButton), onClick = onContinue)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            EmptyElement()
            IllustrationWithHalo(AppImages.AppIllus.GridTilesWithAuthenticator)
            TitleAndDescription(
                title = stringResource(R.string.onBoardingMigrationTitle),
                description = stringResource(R.string.onBoardingMigrationDescription)
            )
        }
    }
}


@Composable
fun MigrationSelectAccounts(
    accounts: () -> List<Account>,
    // skippedIds: () -> Set<Long>,
    isLoading: () -> Boolean,
    modifier: Modifier = Modifier,
    // customization: CrossLoginCustomization = CrossLoginDefaults.customize(),
) {

    // val selectedAccounts = accounts().filter { it.id !in skippedIds() }

    val accounts = accounts()
    val count = accounts.count()

    SideEffect {
        Log.v("Jamy", "MigrationSelectAccounts: $accounts")
    }

    SelectedAccountsButton(
        // customization = customization,
        modifier = modifier,
    ) {
        when {
            count == 1 -> SingleAccount(accounts.single(), Modifier.weight(1.0f), isLoading)
            count > 1 -> MultipleAccounts(accounts, Modifier.weight(1.0f), isLoading)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedAccountsButton(
    //customization: CrossLoginCustomization,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = Margin.Medium),
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.buttonHeight),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(Dimens.largeCornerRadius),
        onClick = { },
        contentPadding = contentPadding,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Margin.Mini),
            horizontalArrangement = Arrangement.spacedBy(Margin.Mini),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            content()

            Icon(
                painter = painterResource(R.drawable.chevron_down),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SingleAccount(
    account: Account,
    modifier: Modifier = Modifier,
    isLoading: () -> Boolean = { false },
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MigrationLoginAvatar(
            modifier = Modifier
                .size(Dimens.bigAvatarSize)
                .clip(CircleShape),
            account = account,
        )

        Spacer(Modifier.width(Margin.Mini))

        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Margin.Mini),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = account.fullName,
                    style = Typography.bodyMedium,
                    // color = customization.colors.titleColor,
                )
                if (isLoading()) {
                    CircularProgressIndicator(Modifier.size(Dimens.smallIconSize), strokeWidth = smallProgressStrokeWidth)
                }
            }
            Text(
                text = account.email,
                style = Typography.bodyRegular,
                // color = customization.colors.descriptionColor,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MultipleAccounts(
    accounts: List<Account>,
    modifier: Modifier = Modifier,
    isLoading: () -> Boolean = { false },
) {

    val count = accounts.count()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Margin.Mini)
    ) {
        if (count == 2) {
            TwoAccountsView(
                accounts,
                // customization.colors.avatarStrokeColor
            )
        } else {
            ThreeAccountsView(
                accounts,
                // customization.colors.avatarStrokeColor
            )
        }

        Text(
            text = pluralStringResource(com.infomaniak.core.crossapplogin.front.R.plurals.selectedAccountCountLabel, count, count),
            style = Typography.bodyMedium,
            // color = customization.colors.titleColor,
        )

        if (isLoading()) {
            CircularProgressIndicator(modifier = Modifier.size(Dimens.smallIconSize), strokeWidth = smallProgressStrokeWidth)
        }
    }
}

@Composable
internal fun MigrationLoginAvatar(
    account: Account,
    modifier: Modifier = Modifier,
    strokeColor: Color? = null
) {
    val localAvatarColors = LocalAvatarColors.current
    val context = LocalContext.current
    val unauthenticatedImageLoader = remember(context) { ImageLoaderProvider.newImageLoader(context) }

    val avatarColors = AvatarColors(
        containerColor = getBackgroundColorResBasedOnId(account.id.toInt(), localAvatarColors.containerColors),
        contentColor = localAvatarColors.contentColor,
    )

    Avatar(
        avatarType = AvatarType.getUrlOrInitials(
            account.avatarUrl?.let { AvatarUrlData(it, unauthenticatedImageLoader) },
            initials = account.initials,
            colors = avatarColors,
        ),
        modifier = modifier,
        border = strokeColor?.let { BorderStroke(width = 1.dp, color = it) },
    )
}

@Composable
internal fun TwoAccountsView(
    accounts: List<Account>,
    // avatarStrokeColor: Color,
) {
    Box(
        modifier = Modifier.size(
            width = Dimens.avatarsBoxWidth,
            height = Dimens.avatarsBoxHeight,
        ),
    ) {

        // Right
        MigrationLoginAvatar(
            modifier = Modifier
                .size(Dimens.avatarsBoxHeight)
                .align(Alignment.CenterEnd),
            account = accounts[1],
            // strokeColor = avatarStrokeColor,
        )

        // Left
        MigrationLoginAvatar(
            modifier = Modifier
                .size(Dimens.avatarsBoxHeight)
                .align(Alignment.CenterStart),
            account = accounts[0],
            // strokeColor = avatarStrokeColor,
        )
    }
}

@Composable
internal fun ThreeAccountsView(
    accounts: List<Account>,
    // avatarStrokeColor: Color,
) {
    Box(contentAlignment = Alignment.Center) {

        Row {
            // Left
            MigrationLoginAvatar(
                modifier = Modifier.size(Dimens.iconSize),
                account = accounts[1],
                // strokeColor = avatarStrokeColor,
            )

            Spacer(Modifier.width(width = Dimens.avatarsBoxWidth - (Dimens.iconSize + Dimens.iconSize)))

            // Right
            MigrationLoginAvatar(
                modifier = Modifier.size(Dimens.iconSize),
                account = accounts[2],
                // strokeColor = avatarStrokeColor,
            )
        }

        // Center
        MigrationLoginAvatar(
            modifier = Modifier.size(Dimens.avatarsBoxHeight),
            account = accounts[0],
            // strokeColor = avatarStrokeColor,
        )
    }
}

@PreviewSmallWindow
@Composable
private fun MigrationScreenPreview() {
    AuthenticatorTheme {
        MigrationScreen(
            accounts = { emptyList() },
            onContinue = {},
        )
    }
}

