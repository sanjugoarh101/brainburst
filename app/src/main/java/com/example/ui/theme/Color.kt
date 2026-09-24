package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Neon / Electric Palette
val NeonViolet = Color(0xFF8A2BE2)
val NeonPurple = Color(0xFF9D4EDD)
val NeonCyan = Color(0xFF00F5D4)
val NeonPink = Color(0xFFFF007F)
val NeonAmber = Color(0xFFFFB703)
val NeonGreen = Color(0xFF06D6A0)
val ElectricBlue = Color(0xFF3A86FF)

// Dark Gamified Backgrounds
val DarkCanvas = Color(0xFF0D061A)
val DarkSurface = Color(0xFF180E2B)
val DarkSurfaceElevated = Color(0xFF24153E)
val DarkBorder = Color(0xFF3B2361)

// Coin & Gem Accents
val GoldCoin = Color(0xFFFFD166)
val GoldCoinDark = Color(0xFFF48C06)
val GemCyan = Color(0xFF48CAE4)
val GemCyanDark = Color(0xFF0096C7)

// Text Colors
val TextPrimary = Color(0xFFF8F9FA)
val TextSecondary = Color(0xFFB8B8D1)
val TextTertiary = Color(0xFF7E7E9A)

// Theme Presets
enum class GameThemePreset(
    val id: String,
    val displayName: String,
    val primary: Color,
    val secondary: Color,
    val surface: Color,
    val background: Color,
    val accent: Color
) {
    CYBER_VIOLET("cyber_violet", "Cyber Violet", NeonViolet, NeonCyan, DarkSurfaceElevated, DarkCanvas, NeonPink),
    EMERALD_ZEN("emerald_zen", "Emerald Zen", Color(0xFF10B981), Color(0xFF34D399), Color(0xFF064E3B), Color(0xFF022C22), Color(0xFFFBBF24)),
    SUNSET_VAPOR("sunset_vapor", "Sunset Wave", Color(0xFFFF5E7E), Color(0xFFFF9966), Color(0xFF381534), Color(0xFF1E0A22), Color(0xFFFFD166)),
    MIDNIGHT_GOLD("midnight_gold", "Midnight Gold", Color(0xFFF59E0B), Color(0xFFFCD34D), Color(0xFF1F1D2B), Color(0xFF0F0E17), Color(0xFF60A5FA))
}
