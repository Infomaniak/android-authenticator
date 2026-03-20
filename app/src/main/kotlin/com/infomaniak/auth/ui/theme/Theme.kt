package com.infomaniak.auth.ui.theme

import android.app.Activity
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.infomaniak.auth.ui.theme.AppDimens.DefaultCornerRadius
import com.infomaniak.auth.ui.theme.color.AvatarColorsDark
import com.infomaniak.auth.ui.theme.color.AvatarColorsLight
import com.infomaniak.auth.ui.theme.color.CustomColorScheme
import com.infomaniak.auth.ui.theme.color.StatusColorScheme
import com.infomaniak.auth.ui.theme.color.brand10
import com.infomaniak.auth.ui.theme.color.brand15
import com.infomaniak.auth.ui.theme.color.brand20
import com.infomaniak.auth.ui.theme.color.brand25
import com.infomaniak.auth.ui.theme.color.brand30
import com.infomaniak.auth.ui.theme.color.brand35
import com.infomaniak.auth.ui.theme.color.brand40
import com.infomaniak.auth.ui.theme.color.brand5
import com.infomaniak.auth.ui.theme.color.brand70
import com.infomaniak.auth.ui.theme.color.brand80
import com.infomaniak.auth.ui.theme.color.brand90
import com.infomaniak.auth.ui.theme.color.brand99
import com.infomaniak.auth.ui.theme.color.darkCustomScheme
import com.infomaniak.auth.ui.theme.color.darkStatusColorScheme
import com.infomaniak.auth.ui.theme.color.lightCustomScheme
import com.infomaniak.auth.ui.theme.color.lightStatusColorScheme
import com.infomaniak.auth.ui.theme.color.neutral0
import com.infomaniak.auth.ui.theme.color.neutral10
import com.infomaniak.auth.ui.theme.color.neutral100
import com.infomaniak.auth.ui.theme.color.neutral12
import com.infomaniak.auth.ui.theme.color.neutral17
import com.infomaniak.auth.ui.theme.color.neutral20
import com.infomaniak.auth.ui.theme.color.neutral22
import com.infomaniak.auth.ui.theme.color.neutral24
import com.infomaniak.auth.ui.theme.color.neutral30
import com.infomaniak.auth.ui.theme.color.neutral4
import com.infomaniak.auth.ui.theme.color.neutral5
import com.infomaniak.auth.ui.theme.color.neutral50
import com.infomaniak.auth.ui.theme.color.neutral6
import com.infomaniak.auth.ui.theme.color.neutral60
import com.infomaniak.auth.ui.theme.color.neutral80
import com.infomaniak.auth.ui.theme.color.neutral87
import com.infomaniak.auth.ui.theme.color.neutral90
import com.infomaniak.auth.ui.theme.color.neutral94
import com.infomaniak.auth.ui.theme.color.neutral95
import com.infomaniak.auth.ui.theme.color.neutral98
import com.infomaniak.auth.ui.theme.color.red15
import com.infomaniak.auth.ui.theme.color.red20
import com.infomaniak.auth.ui.theme.color.red40
import com.infomaniak.auth.ui.theme.color.red80
import com.infomaniak.auth.ui.theme.color.red95
import com.infomaniak.auth.ui.theme.color.red99
import com.infomaniak.core.avatar.AvatarColors
import com.infomaniak.core.avatar.LocalAvatarColors
import com.infomaniak.core.privacymanagement.theme.LocalPrivacyManagementTheme
import com.infomaniak.core.privacymanagement.theme.PrivacyManagementTheme
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.theme.LocalIsThemeDarkMode

// Never access it directly outside the theme setup
val lightScheme = lightColorScheme(
    primary = brand30,
    onPrimary = brand99,
    primaryContainer = brand40,
    onPrimaryContainer = brand80,
    primaryFixed = brand90,
    onPrimaryFixed = brand10,
    primaryFixedDim = brand80,
    onPrimaryFixedVariant = brand30,
    secondary = brand40,
    onSecondary = brand99,
    secondaryContainer = brand70,
    onSecondaryContainer = brand25,
    secondaryFixed = brand90,
    onSecondaryFixed = brand5,
    secondaryFixedDim = brand80,
    onSecondaryFixedVariant = brand30,
    tertiary = brand40,
    onTertiary = brand99,
    tertiaryContainer = brand70,
    onTertiaryContainer = brand25,
    tertiaryFixed = brand90,
    onTertiaryFixed = brand5,
    tertiaryFixedDim = brand80,
    onTertiaryFixedVariant = brand30,
    error = red40,
    onError = red99,
    errorContainer = red80,
    onErrorContainer = red15,
    background = neutral94,
    onBackground = neutral5,
    surface = neutral95,
    onSurface = neutral4,
    surfaceVariant = neutral87,
    onSurfaceVariant = neutral30,
    surfaceTint = brand35,
    surfaceDim = neutral90,
    surfaceBright = neutral98,
    surfaceContainerLowest = neutral100,
    surfaceContainerLow = neutral95,
    surfaceContainer = neutral90,
    surfaceContainerHigh = neutral90,
    surfaceContainerHighest = neutral87,
    scrim = neutral0,
    outline = neutral50,
    outlineVariant = neutral87,
    inverseSurface = neutral20,
    inverseOnSurface = neutral90,
    inversePrimary = brand80,
)

private val darkScheme = darkColorScheme(
    primary = brand80,
    onPrimary = brand20,
    primaryContainer = brand40,
    onPrimaryContainer = brand90,
    primaryFixed = brand90,
    onPrimaryFixed = brand10,
    primaryFixedDim = brand80,
    onPrimaryFixedVariant = brand30,
    secondary = brand80,
    onSecondary = brand10,
    secondaryContainer = brand70,
    onSecondaryContainer = brand10,
    secondaryFixed = brand90,
    onSecondaryFixed = brand5,
    secondaryFixedDim = brand80,
    onSecondaryFixedVariant = brand15,
    tertiary = brand80,
    onTertiary = brand10,
    tertiaryContainer = brand70,
    onTertiaryContainer = brand10,
    tertiaryFixed = brand90,
    onTertiaryFixed = brand5,
    tertiaryFixedDim = brand80,
    onTertiaryFixedVariant = brand15,
    error = red80,
    onError = red20,
    errorContainer = red20,
    onErrorContainer = red95,
    background = neutral5,
    onBackground = neutral90,
    surface = neutral6,
    onSurface = neutral90,
    surfaceVariant = neutral30,
    onSurfaceVariant = neutral80,
    surfaceTint = brand80,
    surfaceDim = neutral6,
    surfaceBright = neutral24,
    surfaceContainerLowest = neutral4,
    surfaceContainerLow = neutral10,
    surfaceContainer = neutral12,
    surfaceContainerHigh = neutral17,
    surfaceContainerHighest = neutral22,
    scrim = neutral0,
    outline = neutral60,
    outlineVariant = neutral30,
    inverseSurface = neutral90,
    inverseOnSurface = neutral20,
    inversePrimary = brand40,
)

val LocalCustomColorScheme: ProvidableCompositionLocal<CustomColorScheme> = staticCompositionLocalOf { CustomColorScheme() }
val LocalStatusColorScheme: ProvidableCompositionLocal<StatusColorScheme> = staticCompositionLocalOf { StatusColorScheme() }

@Composable
fun AuthenticatorTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && SDK_INT >= 31 -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        isDarkTheme -> darkScheme
        else -> lightScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDarkTheme
                isAppearanceLightNavigationBars = !isDarkTheme
            }
        }
    }

    val customColors = if (isDarkTheme) darkCustomScheme else lightCustomScheme
    val statusColors = if (isDarkTheme) darkStatusColorScheme else lightStatusColorScheme
    val privacyManagementTheme = PrivacyManagementTheme(
        trackerContainerColor = customColors.sectionBackground,
        trackerContainerContentColor = colorScheme.onSurface,
        trackerContainerShape = RoundedCornerShape(DefaultCornerRadius),
        trackerContainerPadding = PaddingValues(Margin.Medium, 0.dp)
    )
    val avatarColors = if (isDarkTheme) AvatarColorsDark else AvatarColorsLight

    CompositionLocalProvider(
        LocalStatusColorScheme provides statusColors,
        LocalCustomColorScheme provides customColors,
        LocalIsThemeDarkMode provides isDarkTheme,
        LocalPrivacyManagementTheme provides privacyManagementTheme,
        LocalAvatarColors provides AvatarColors(avatarColors.colorList, customColors.sectionBackground),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

object AuthenticatorTheme {
    val statusColors: StatusColorScheme
        @Composable
        get() = LocalStatusColorScheme.current
    val customColors: CustomColorScheme
        @Composable
        get() = LocalCustomColorScheme.current
    val materialColors: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme
}
