package com.sharewind.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Neutral900,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = Neutral900,
    secondary = Secondary,
    onSecondary = Color.White,
    tertiary = Accent,
    onTertiary = Neutral900,
    error = Error,
    onError = Color.White,
    background = Neutral900,
    onBackground = Neutral100,
    surface = Neutral900,
    onSurface = Neutral100
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryHover,
    secondary = Secondary,
    onSecondary = Color.White,
    tertiary = Accent,
    onTertiary = Neutral900,
    error = Error,
    onError = Color.White,
    background = Background,
    onBackground = Neutral900,
    surface = Color.White,
    onSurface = Neutral900
)

@Composable
fun ShareWindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
