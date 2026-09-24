package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val username: String = "Brainiac",
    val title: String = "Cognitive Explorer",
    val avatarEmoji: String = "⚡",
    val coins: Int = 650,
    val gems: Int = 30,
    val level: Int = 3,
    val xp: Int = 340,
    val brainPowerIndex: Int = 820,
    val mathHighScore: Int = 1250,
    val wordHighScore: Int = 980,
    val memoryHighScore: Int = 1100,
    val workoutHighScore: Int = 2400,
    val currentStreakDays: Int = 4,
    val lastStreakClaimDay: Int = 3,
    val freezeCount: Int = 2,
    val hintCount: Int = 3,
    val shieldCount: Int = 1,
    val activeThemeId: String = "cyber_violet",
    val unlockedThemes: String = "cyber_violet,emerald_zen"
)

/**
 * Tracks the 24-hour cycle state for daily challenges.
 * Enforces the business rule: players can only complete one set of challenges per 24-hour cycle.
 */
@Entity(tableName = "daily_challenge_cycles")
data class DailyChallengeCycleEntity(
    @PrimaryKey val cycleDate: String, // e.g. "2026-09-24"
    val cycleStartTimeMillis: Long,
    val cycleEndTimeMillis: Long, // End of the 24-hour window
    val isSetCompleted: Boolean = false, // True once all challenges in this cycle's set are completed
    val completedAt: Long? = null,
    val allRewardsClaimed: Boolean = false,
    val bonusClaimed: Boolean = false,
    val bonusCoins: Int = 500,
    val bonusGems: Int = 20
)

/**
 * Stores each individual daily game challenge scoped to a specific 24-hour cycle.
 */
@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey val id: String, // Unique identifier e.g. "2026-09-24_1"
    val cycleDate: String, // Associates this challenge with its 24h cycle
    val challengeIndex: Int, // Position: 1, 2, 3...
    val challengeType: String, // e.g. "SPEED_MATH", "COMBO_STREAK", "WORD_BLITZ", "MEMORY_MATRIX"
    val title: String,
    val description: String,
    val targetValue: Int,
    val currentProgress: Int,
    val rewardCoins: Int,
    val rewardGems: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val completedAt: Long? = null
)

@Entity(tableName = "leaderboard_players")
data class LeaderboardPlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rank: Int,
    val username: String,
    val title: String,
    val avatarEmoji: String,
    val score: Int,
    val league: String,
    val isUser: Boolean = false
)
