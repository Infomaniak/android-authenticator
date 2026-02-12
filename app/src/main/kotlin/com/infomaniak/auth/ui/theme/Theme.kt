package com.infomaniak.auth.ui.theme

import android.app.Activity
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.core.view.WindowCompat
import com.infomaniak.auth.ui.theme.color.CustomColorScheme
import com.infomaniak.auth.ui.theme.color.StatusColorScheme
import com.infomaniak.auth.ui.theme.color.brand10
import com.infomaniak.auth.ui.theme.color.brand20
import com.infomaniak.auth.ui.theme.color.brand30
import com.infomaniak.auth.ui.theme.color.brand40
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
import com.infomaniak.auth.ui.theme.color.neutral17
import com.infomaniak.auth.ui.theme.color.neutral20
import com.infomaniak.auth.ui.theme.color.neutral22
import com.infomaniak.auth.ui.theme.color.neutral24
import com.infomaniak.auth.ui.theme.color.neutral30
import com.infomaniak.auth.ui.theme.color.neutral4
import com.infomaniak.auth.ui.theme.color.neutral5
import com.infomaniak.auth.ui.theme.color.neutral6
import com.infomaniak.auth.ui.theme.color.neutral60
import com.infomaniak.auth.ui.theme.color.neutral80
import com.infomaniak.auth.ui.theme.color.neutral87
import com.infomaniak.auth.ui.theme.color.neutral90
import com.infomaniak.auth.ui.theme.color.neutral92
import com.infomaniak.auth.ui.theme.color.neutral94
import com.infomaniak.auth.ui.theme.color.neutral95
import com.infomaniak.auth.ui.theme.color.neutral96
import com.infomaniak.auth.ui.theme.color.neutral98
import com.infomaniak.auth.ui.theme.color.neutral99
import com.infomaniak.auth.ui.theme.color.red10
import com.infomaniak.auth.ui.theme.color.red20
import com.infomaniak.auth.ui.theme.color.red40
import com.infomaniak.auth.ui.theme.color.red50
import com.infomaniak.auth.ui.theme.color.red60
import com.infomaniak.auth.ui.theme.color.red80
import com.infomaniak.auth.ui.theme.color.red99
import com.infomaniak.core.ui.compose.theme.LocalIsThemeDarkMode

// Never access it directly outside of the theme setup
val lightScheme = lightColorScheme(
    primary = brand30,
    onPrimary = brand99,
    primaryContainer = brand40,
    onPrimaryContainer = brand90,
    secondary = brand40,
    onSecondary = brand99,
    secondaryContainer = brand70,
    onSecondaryContainer = brand10,
    tertiary = brand40,
    onTertiary = brand99,
    tertiaryContainer = brand70,
    onTertiaryContainer = brand10,
    error = red40,
    onError = red99,
    errorContainer = red50,
    onErrorContainer = red99,
    background = neutral98,
    onBackground = neutral10,
    surface = neutral92,
    onSurface = neutral4,
    surfaceVariant = neutral87,
    onSurfaceVariant = neutral30,
    outline = neutral60,
    outlineVariant = neutral30,
    scrim = neutral0,
    inverseSurface = neutral20,
    inverseOnSurface = neutral90,
    inversePrimary = brand80,
    surfaceDim = neutral90,
    surfaceBright = neutral98,
    surfaceContainerLowest = neutral100,
    surfaceContainerLow = neutral95,
    surfaceContainer = neutral90,
    surfaceContainerHigh = neutral90,
    surfaceContainerHighest = neutral87,
)

private val darkScheme = darkColorScheme(
    primary = brand80,
    onPrimary = brand20,
    primaryContainer = brand40,
    onPrimaryContainer = brand90,
    secondary = brand80,
    onSecondary = brand10,
    secondaryContainer = brand70,
    onSecondaryContainer = brand10,
    tertiary = brand80,
    onTertiary = brand10,
    tertiaryContainer = brand70,
    onTertiaryContainer = brand10,
    error = red80,
    onError = red20,
    errorContainer = red60,
    onErrorContainer = red10,
    background = neutral5,
    onBackground = neutral90,
    surface = neutral6,
    onSurface = neutral90,
    surfaceVariant = neutral30,
    onSurfaceVariant = neutral80,
    outline = neutral60,
    outlineVariant = neutral30,
    scrim = neutral0,
    inverseSurface = neutral90,
    inverseOnSurface = neutral20,
    inversePrimary = brand40,
    surfaceDim = neutral6,
    surfaceBright = neutral24,
    surfaceContainerLowest = neutral4,
    surfaceContainerLow = neutral10,
    surfaceContainer = neutral12,
    surfaceContainerHigh = neutral17,
    surfaceContainerHighest = neutral22,
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

    CompositionLocalProvider(
        LocalStatusColorScheme provides statusColors,
        LocalCustomColorScheme provides customColors,
        LocalIsThemeDarkMode provides isDarkTheme
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
