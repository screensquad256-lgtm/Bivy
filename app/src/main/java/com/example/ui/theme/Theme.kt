package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BivyDarkColorScheme = darkColorScheme(
    primary = BivyGold,
    onPrimary = Color(0xFF1E1500),
    primaryContainer = Color(0xFF3E2C00),
    onPrimaryContainer = BivyGoldLight,
    secondary = BivyCyan,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Color(0xFF80F0FF),
    tertiary = BivyViolet,
    onTertiary = Color.White,
    background = BivyBackground,
    onBackground = BivyTextPrimary,
    surface = BivySurface,
    onSurface = BivyTextPrimary,
    surfaceVariant = BivySurfaceElevated,
    onSurfaceVariant = BivyTextSecondary,
    outline = BivyBorder,
    outlineVariant = BivyBorderSubtle,
    error = BivyCrimson,
    onError = Color.White
)

@Composable
fun BivyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BivyDarkColorScheme,
        typography = Typography,
        content = content
    )
}
