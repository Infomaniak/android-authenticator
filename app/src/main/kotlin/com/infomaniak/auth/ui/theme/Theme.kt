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
import com.infomaniak.core.ui.compose.theme.LocalIsThemeDarkMode

// Never access it directly outside of the theme setup
val lightScheme = lightColorScheme(
    primary = brand30,
    onPrimary = brand99,
    primaryContainer = brand40,
    onPrimaryContainer = brand80,
    secondary = brand40,
    onSecondary = brand99,
    secondaryContainer = brand70,
    onSecondaryContainer = brand25,
    tertiary = brand40,
    onTertiary = brand99,
    tertiaryContainer = brand70,
    onTertiaryContainer = brand25,
    error = red40,
    onError = red99,
    errorContainer = red50,
    onErrorContainer = red99,
    background = neutral94,
    onBackground = neutral5,
    surface = neutral92,
    onSurface = neutral4,
    surfaceVariant = neutral87,
    onSurfaceVariant = neutral30,
    outline = neutral50,
    outlineVariant = neutral80,
    scrim = neutral0,
    inverseSurface = neutral20,
    inverseOnSurface = neutral95,
    inversePrimary = brand80,
    surfaceDim = neutral87,
    surfaceBright = neutral98,
    surfaceContainerLowest = neutral99,
    surfaceContainerLow = neutral96,
    surfaceContainer = neutral94,
    surfaceContainerHigh = neutral92,
    surfaceContainerHighest = neutral90,
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
    surfaceContainer = neutral10,
    surfaceContainerHigh = neutral17,
    surfaceContainerHighest = neutral22,
)

val LocalCustomColorScheme: ProvidableCompositionLocal<CustomColorScheme> = staticCompositionLocalOf { CustomColorScheme() }

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

    CompositionLocalProvider(
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
    val colors: CustomColorScheme
        @Composable
        get() = LocalCustomColorScheme.current
    val materialColors: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme
}
