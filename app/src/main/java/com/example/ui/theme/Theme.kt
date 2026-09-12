package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val YouTubeDarkColorScheme = darkColorScheme(
    primary = YouTubeRed,
    onPrimary = Color.White,
    primaryContainer = YouTubeRedDark,
    onPrimaryContainer = Color.White,
    secondary = YouTubeBlue,
    onSecondary = Color.Black,
    background = YouTubeDarkBackground,
    onBackground = YouTubeDarkTextPrimary,
    surface = YouTubeDarkSurface,
    onSurface = YouTubeDarkTextPrimary,
    surfaceVariant = YouTubeDarkSurfaceVariant,
    onSurfaceVariant = YouTubeDarkTextSecondary,
    outline = YouTubeDarkBorder
)

private val YouTubeLightColorScheme = lightColorScheme(
    primary = YouTubeRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE5E5),
    onPrimaryContainer = YouTubeRedDark,
    secondary = YouTubeBlue,
    onSecondary = Color.White,
    background = YouTubeLightBackground,
    onBackground = YouTubeLightTextPrimary,
    surface = YouTubeLightSurface,
    onSurface = YouTubeLightTextPrimary,
    surfaceVariant = YouTubeLightSurfaceVariant,
    onSurfaceVariant = YouTubeLightTextSecondary,
    outline = YouTubeLightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> YouTubeDarkColorScheme
        else -> YouTubeLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
