package com.example.data.model

enum class GameMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val baseDurationSeconds: Int,
    val accentHex: Long
) {
    SPEED_MATH(
        id = "speed_math",
        title = "Speed Math",
        subtitle = "Rapid mental calculations",
        category = "Agility",
        baseDurationSeconds = 40,
        accentHex = 0xFFFF007F
    ),
    WORD_BLITZ(
        id = "word_blitz",
        title = "Word Blitz",
        subtitle = "Anagrams & vocabulary puzzles",
        category = "Language",
        baseDurationSeconds = 45,
        accentHex = 0xFF00F5D4
    ),
    MEMORY_MATRIX(
        id = "memory_matrix",
        title = "Memory Matrix",
        subtitle = "Visual pattern recall",
        category = "Focus",
        baseDurationSeconds = 45,
        accentHex = 0xFFFFB703
    ),
    DAILY_WORKOUT(
        id = "daily_workout",
        title = "Daily Mind Workout",
        subtitle = "Complete all 3 disciplines for triple XP",
        category = "Full Gym",
        baseDurationSeconds = 60,
        accentHex = 0xFF8A2BE2
    )
}

enum class PowerUpType(
    val id: String,
    val displayName: String,
    val description: String,
    val coinCost: Int
) {
    FREEZE_TIME("freeze_time", "Time Freeze", "Pauses countdown for 6 seconds", 250),
    HINT_5050("hint_5050", "50/50 Hint", "Eliminates 2 wrong choices", 200),
    SHIELD("shield", "Combo Shield", "Protects combo from 1 mistake", 300)
}

data class MathQuestion(
    val expression: String,
    val options: List<String>,
    val correctIndex: Int
)

data class WordQuestion(
    val scrambled: String,
    val hint: String,
    val options: List<String>,
    val correctIndex: Int
)

data class MemoryMatrixQuestion(
    val gridSize: Int = 3,
    val activeIndices: Set<Int>
)

data class GameResult(
    val mode: GameMode,
    val finalScore: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val maxCombo: Int,
    val coinsEarned: Int,
    val gemsEarned: Int,
    val xpGained: Int,
    val isNewHighScore: Boolean = false
)

data class MicrotransactionItem(
    val id: String,
    val title: String,
    val description: String,
    val priceUsd: String,
    val coinsAmount: Int,
    val gemsAmount: Int,
    val isBundle: Boolean = false,
    val badgeLabel: String? = null
)
