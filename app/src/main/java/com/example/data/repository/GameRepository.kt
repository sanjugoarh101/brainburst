package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.db.AppDatabase
import com.example.data.db.DailyChallengeCycleEntity
import com.example.data.db.DailyChallengeEntity
import com.example.data.db.LeaderboardPlayerEntity
import com.example.data.db.UserStatsEntity
import com.example.data.model.GameMode
import com.example.data.model.GameResult
import com.example.data.model.PowerUpType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.max

class GameRepository(private val dao: AppDao) {

    val userStats: Flow<UserStatsEntity?> = dao.getUserStats()
    val leaderboard: Flow<List<LeaderboardPlayerEntity>> = dao.getLeaderboard()

    fun getActiveCycle(): Flow<DailyChallengeCycleEntity?> {
        val today = AppDatabase.getTodayCycleDate()
        return dao.getCycle(today)
    }

    fun getChallengesForActiveCycle(): Flow<List<DailyChallengeEntity>> {
        val today = AppDatabase.getTodayCycleDate()
        return dao.getChallengesForCycle(today)
    }

    suspend fun ensureInitialized() {
        val currentStats = dao.getUserStats().firstOrNull()
        if (currentStats == null) {
            AppDatabase.seedInitialData(dao)
            return
        }

        // Ensure active 24-hour cycle exists for today
        checkAndCreateTodayCycle()
    }

    suspend fun checkAndCreateTodayCycle(): DailyChallengeCycleEntity {
        val todayDate = AppDatabase.getTodayCycleDate()
        val existingCycle = dao.getCycleDirect(todayDate)
        if (existingCycle != null) {
            return existingCycle
        }

        // New 24-hour cycle starts now!
        val (startTime, endTime) = AppDatabase.getTodayCycleTimes()
        val newCycle = DailyChallengeCycleEntity(
            cycleDate = todayDate,
            cycleStartTimeMillis = startTime,
            cycleEndTimeMillis = endTime,
            isSetCompleted = false,
            allRewardsClaimed = false,
            bonusClaimed = false,
            bonusCoins = 500,
            bonusGems = 20
        )
        dao.insertCycle(newCycle)

        // Generate the 1 set of challenges for this 24-hour cycle
        val challenges = listOf(
            DailyChallengeEntity(
                id = "${todayDate}_1",
                cycleDate = todayDate,
                challengeIndex = 1,
                challengeType = "SPEED_MATH",
                title = "Speed Calculation",
                description = "Score over 1,000 points in Speed Math",
                targetValue = 1000,
                currentProgress = 0,
                rewardCoins = 150,
                rewardGems = 5,
                isCompleted = false,
                isClaimed = false
            ),
            DailyChallengeEntity(
                id = "${todayDate}_2",
                cycleDate = todayDate,
                challengeIndex = 2,
                challengeType = "COMBO_STREAK",
                title = "Fever Unleashed",
                description = "Reach a 5x Combo streak in any mode",
                targetValue = 5,
                currentProgress = 0,
                rewardCoins = 250,
                rewardGems = 10,
                isCompleted = false,
                isClaimed = false
            ),
            DailyChallengeEntity(
                id = "${todayDate}_3",
                cycleDate = todayDate,
                challengeIndex = 3,
                challengeType = "WORD_BLITZ",
                title = "Vocabulary Master",
                description = "Solve 8 words in Word Blitz without mistakes",
                targetValue = 8,
                currentProgress = 0,
                rewardCoins = 300,
                rewardGems = 15,
                isCompleted = false,
                isClaimed = false
            )
        )
        dao.insertChallenges(challenges)

        return newCycle
    }

    suspend fun recordGameResult(result: GameResult) {
        val current = dao.getUserStats().firstOrNull() ?: UserStatsEntity()
        val newCoins = current.coins + result.coinsEarned
        val newGems = current.gems + result.gemsEarned
        val newXp = current.xp + result.xpGained
        val newLevel = 1 + (newXp / 300)
        val newBpi = current.brainPowerIndex + (result.finalScore / 50).coerceAtMost(35)

        val updatedMathHigh = if (result.mode == GameMode.SPEED_MATH) max(current.mathHighScore, result.finalScore) else current.mathHighScore
        val updatedWordHigh = if (result.mode == GameMode.WORD_BLITZ) max(current.wordHighScore, result.finalScore) else current.wordHighScore
        val updatedMemHigh = if (result.mode == GameMode.MEMORY_MATRIX) max(current.memoryHighScore, result.finalScore) else current.memoryHighScore
        val updatedWorkoutHigh = if (result.mode == GameMode.DAILY_WORKOUT) max(current.workoutHighScore, result.finalScore) else current.workoutHighScore

        val updated = current.copy(
            coins = newCoins,
            gems = newGems,
            xp = newXp,
            level = newLevel,
            brainPowerIndex = newBpi,
            mathHighScore = updatedMathHigh,
            wordHighScore = updatedWordHigh,
            memoryHighScore = updatedMemHigh,
            workoutHighScore = updatedWorkoutHigh
        )
        dao.updateUserStats(updated)

        // Update daily challenges for the active 24-hour cycle
        val today = AppDatabase.getTodayCycleDate()
        val cycle = dao.getCycleDirect(today) ?: checkAndCreateTodayCycle()

        // BUSINESS RULE: Players can only complete one set of challenges per 24-hour cycle.
        // If the current cycle set is already marked completed, no further challenge updates occur until the next 24h cycle!
        if (!cycle.isSetCompleted) {
            val challenges = dao.getChallengesForCycleDirect(today)
            var allNowCompleted = true

            challenges.forEach { challenge ->
                if (!challenge.isCompleted) {
                    var progressAdded = 0
                    when (challenge.challengeType) {
                        "SPEED_MATH" -> if (result.mode == GameMode.SPEED_MATH) progressAdded = result.finalScore
                        "COMBO_STREAK" -> if (result.maxCombo >= 5) progressAdded = 5
                        "WORD_BLITZ" -> if (result.mode == GameMode.WORD_BLITZ) progressAdded = result.correctCount
                    }
                    if (progressAdded > 0) {
                        val newProgress = (challenge.currentProgress + progressAdded).coerceAtMost(challenge.targetValue)
                        val isComplete = newProgress >= challenge.targetValue
                        dao.updateChallenge(
                            challenge.copy(
                                currentProgress = newProgress,
                                isCompleted = isComplete,
                                completedAt = if (isComplete) System.currentTimeMillis() else null
                            )
                        )
                        if (!isComplete) allNowCompleted = false
                    } else {
                        allNowCompleted = false
                    }
                }
            }

            // If all challenges in the set are now completed, lock the set for this 24-hour cycle!
            val totalInCycle = dao.countTotalChallengesInCycle(today)
            val completedInCycle = dao.countCompletedChallengesInCycle(today)
            if (totalInCycle > 0 && completedInCycle >= totalInCycle) {
                dao.updateCycle(
                    cycle.copy(
                        isSetCompleted = true,
                        completedAt = System.currentTimeMillis()
                    )
                )
            }
        }

        // Leaderboard position check
        val overallBest = max(max(updatedMathHigh, updatedWordHigh), max(updatedMemHigh, updatedWorkoutHigh))
        val newRank = when {
            overallBest >= 3900 -> 1
            overallBest >= 3600 -> 2
            overallBest >= 3300 -> 3
            overallBest >= 3000 -> 4
            overallBest >= 2700 -> 5
            overallBest >= 2500 -> 6
            overallBest >= 2300 -> 7
            else -> 8
        }
        dao.updateUserLeaderboardScore(overallBest, newRank)
    }

    suspend fun claimChallengeReward(challengeId: String): Boolean {
        val challenge = dao.getChallengeById(challengeId) ?: return false
        if (challenge.isCompleted && !challenge.isClaimed) {
            dao.updateChallenge(challenge.copy(isClaimed = true))
            val current = dao.getUserStats().firstOrNull() ?: return false
            dao.updateUserStats(
                current.copy(
                    coins = current.coins + challenge.rewardCoins,
                    gems = current.gems + challenge.rewardGems
                )
            )

            // Check if all challenges in cycle are claimed
            val cycle = dao.getCycleDirect(challenge.cycleDate)
            if (cycle != null) {
                val cycleChallenges = dao.getChallengesForCycleDirect(challenge.cycleDate)
                val allClaimed = cycleChallenges.all { it.isClaimed || it.id == challengeId }
                if (allClaimed) {
                    dao.updateCycle(cycle.copy(allRewardsClaimed = true))
                }
            }
            return true
        }
        return false
    }

    suspend fun claimSetCompletionBonus(cycleDate: String): Boolean {
        val cycle = dao.getCycleDirect(cycleDate) ?: return false
        if (cycle.isSetCompleted && !cycle.bonusClaimed) {
            dao.updateCycle(cycle.copy(bonusClaimed = true))
            val current = dao.getUserStats().firstOrNull() ?: return false
            dao.updateUserStats(
                current.copy(
                    coins = current.coins + cycle.bonusCoins,
                    gems = current.gems + cycle.bonusGems
                )
            )
            return true
        }
        return false
    }

    suspend fun claimDailyStreak(day: Int, rewardCoins: Int, rewardGems: Int) {
        val current = dao.getUserStats().firstOrNull() ?: return
        dao.updateUserStats(
            current.copy(
                lastStreakClaimDay = day,
                coins = current.coins + rewardCoins,
                gems = current.gems + rewardGems
            )
        )
    }

    suspend fun buyPowerUp(type: PowerUpType): Boolean {
        val current = dao.getUserStats().firstOrNull() ?: return false
        if (current.coins < type.coinCost) return false

        val updated = when (type) {
            PowerUpType.FREEZE_TIME -> current.copy(
                coins = current.coins - type.coinCost,
                freezeCount = current.freezeCount + 1
            )
            PowerUpType.HINT_5050 -> current.copy(
                coins = current.coins - type.coinCost,
                hintCount = current.hintCount + 1
            )
            PowerUpType.SHIELD -> current.copy(
                coins = current.coins - type.coinCost,
                shieldCount = current.shieldCount + 1
            )
        }
        dao.updateUserStats(updated)
        return true
    }

    suspend fun consumePowerUp(type: PowerUpType): Boolean {
        val current = dao.getUserStats().firstOrNull() ?: return false
        val updated = when (type) {
            PowerUpType.FREEZE_TIME -> {
                if (current.freezeCount <= 0) return false
                current.copy(freezeCount = current.freezeCount - 1)
            }
            PowerUpType.HINT_5050 -> {
                if (current.hintCount <= 0) return false
                current.copy(hintCount = current.hintCount - 1)
            }
            PowerUpType.SHIELD -> {
                if (current.shieldCount <= 0) return false
                current.copy(shieldCount = current.shieldCount - 1)
            }
        }
        dao.updateUserStats(updated)
        return true
    }

    suspend fun applyMicrotransactionPurchase(coins: Int, gems: Int) {
        val current = dao.getUserStats().firstOrNull() ?: return
        dao.updateUserStats(
            current.copy(
                coins = current.coins + coins,
                gems = current.gems + gems
            )
        )
    }

    suspend fun unlockAndEquipTheme(themeId: String, costCoins: Int = 0, costGems: Int = 0): Boolean {
        val current = dao.getUserStats().firstOrNull() ?: return false
        val unlockedList = current.unlockedThemes.split(",").map { it.trim() }

        if (unlockedList.contains(themeId)) {
            dao.updateUserStats(current.copy(activeThemeId = themeId))
            return true
        }

        if (current.coins < costCoins || current.gems < costGems) return false

        val newUnlocked = current.unlockedThemes + ",$themeId"
        dao.updateUserStats(
            current.copy(
                coins = current.coins - costCoins,
                gems = current.gems - costGems,
                activeThemeId = themeId,
                unlockedThemes = newUnlocked
            )
        )
        return true
    }

    suspend fun claimFreeAdGift(coinsBonus: Int = 200, gemsBonus: Int = 5) {
        val current = dao.getUserStats().firstOrNull() ?: return
        dao.updateUserStats(
            current.copy(
                coins = current.coins + coinsBonus,
                gems = current.gems + gemsBonus
            )
        )
    }
}
