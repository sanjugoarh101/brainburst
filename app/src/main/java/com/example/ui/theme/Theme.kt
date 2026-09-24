package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = Color.White,
    primaryContainer = NeonPurple,
    onPrimaryContainer = Color.White,
    secondary = NeonCyan,
    onSecondary = DarkCanvas,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = NeonCyan,
    tertiary = NeonPink,
    onTertiary = Color.White,
    background = DarkCanvas,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

@Composable
fun BrainBurstTheme(
    activePreset: GameThemePreset = GameThemePreset.CYBER_VIOLET,
    content: @Composable () -> Unit
) {
    val dynamicThemeScheme = DarkColorScheme.copy(
        primary = activePreset.primary,
        secondary = activePreset.secondary,
        tertiary = activePreset.accent,
        background = activePreset.background,
        surface = activePreset.surface
    )

    MaterialTheme(
        colorScheme = dynamicThemeScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    BrainBurstTheme(content = content)
}

