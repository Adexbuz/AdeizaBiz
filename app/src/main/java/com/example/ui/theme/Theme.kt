package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MintGreenAccent,
    onPrimary = ForestGreenDark,
    primaryContainer = ForestGreenLight,
    onPrimaryContainer = MintGreenLight,
    secondary = MintGreenAccent,
    onSecondary = ForestGreenDark,
    background = Color(0xFF111815),
    surface = Color(0xFF192420),
    surfaceVariant = Color(0xFF22302A),
    onBackground = LightGreyBg,
    onSurface = LightGreyBg,
    onSurfaceVariant = Color(0xFFCAD7D0),
    outline = Color(0xFF3B4E46),
    error = CoralError
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = PureWhite,
    primaryContainer = MintGreenAccent,
    onPrimaryContainer = ForestGreenPrimary,
    secondary = ForestGreenLight,
    onSecondary = PureWhite,
    secondaryContainer = MintGreenLight,
    onSecondaryContainer = ForestGreenDark,
    background = LightGreyBg,
    surface = PureWhite,
    surfaceVariant = Color(0xFFF0F5F2),
    onBackground = DarkCharcoal,
    onSurface = DarkCharcoal,
    onSurfaceVariant = MediumGrey,
    outline = BorderLight,
    error = CoralError,
    onError = PureWhite
)

@Composable
fun AdeizaBizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
