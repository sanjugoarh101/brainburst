package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@Database(
    entities = [
        UserStatsEntity::class,
        DailyChallengeCycleEntity::class,
        DailyChallengeEntity::class,
        LeaderboardPlayerEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "brainburst_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                seedInitialData(getInstance(context).appDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getTodayCycleDate(): String {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH) + 1
            val d = cal.get(Calendar.DAY_OF_MONTH)
            return String.format(Locale.US, "%04d-%02d-%02d", y, m, d)
        }

        fun getTodayCycleTimes(): Pair<Long, Long> {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val start = cal.timeInMillis

            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            val end = cal.timeInMillis

            return Pair(start, end)
        }

        suspend fun seedInitialData(dao: AppDao) {
            // Seed user profile
            dao.insertUserStats(UserStatsEntity())

            val cycleDate = getTodayCycleDate()
            val (startTime, endTime) = getTodayCycleTimes()

            // Seed active 24-hour cycle
            dao.insertCycle(
                DailyChallengeCycleEntity(
                    cycleDate = cycleDate,
                    cycleStartTimeMillis = startTime,
                    cycleEndTimeMillis = endTime,
                    isSetCompleted = false,
                    allRewardsClaimed = false,
                    bonusClaimed = false,
                    bonusCoins = 500,
                    bonusGems = 20
                )
            )

            // Seed daily challenges for this 24-hour cycle
            dao.insertChallenges(
                listOf(
                    DailyChallengeEntity(
                        id = "${cycleDate}_1",
                        cycleDate = cycleDate,
                        challengeIndex = 1,
                        challengeType = "SPEED_MATH",
                        title = "Speed Calculation",
                        description = "Score over 1,000 points in Speed Math",
                        targetValue = 1000,
                        currentProgress = 450,
                        rewardCoins = 150,
                        rewardGems = 5,
                        isCompleted = false,
                        isClaimed = false
                    ),
                    DailyChallengeEntity(
                        id = "${cycleDate}_2",
                        cycleDate = cycleDate,
                        challengeIndex = 2,
                        challengeType = "COMBO_STREAK",
                        title = "Fever Unleashed",
                        description = "Reach a 5x Combo streak in any mode",
                        targetValue = 5,
                        currentProgress = 3,
                        rewardCoins = 250,
                        rewardGems = 10,
                        isCompleted = false,
                        isClaimed = false
                    ),
                    DailyChallengeEntity(
                        id = "${cycleDate}_3",
                        cycleDate = cycleDate,
                        challengeIndex = 3,
                        challengeType = "WORD_BLITZ",
                        title = "Vocabulary Master",
                        description = "Solve 8 words in Word Blitz without mistakes",
                        targetValue = 8,
                        currentProgress = 5,
                        rewardCoins = 300,
                        rewardGems = 15,
                        isCompleted = false,
                        isClaimed = false
                    )
                )
            )

            // Seed lively leaderboards
            dao.insertLeaderboard(
                listOf(
                    LeaderboardPlayerEntity(1, 1, "NovaBrain_99", "Grandmaster", "👑", 3840, "Diamond League"),
                    LeaderboardPlayerEntity(2, 2, "QuantumDrifter", "Mentalist", "⚡", 3520, "Diamond League"),
                    LeaderboardPlayerEntity(3, 3, "Elena_Wiz", "Word Champion", "🌟", 3210, "Diamond League"),
                    LeaderboardPlayerEntity(4, 4, "Kai_Reflex", "Speedster", "🚀", 2980, "Gold League"),
                    LeaderboardPlayerEntity(5, 5, "Sophie_Math", "Number Architect", "🧠", 2740, "Gold League"),
                    LeaderboardPlayerEntity(6, 6, "PixelRival", "Puzzle Guru", "🎯", 2590, "Gold League"),
                    LeaderboardPlayerEntity(7, 7, "Marcus_Logic", "Strategist", "🔥", 2430, "Gold League"),
                    LeaderboardPlayerEntity(8, 8, "Brainiac (You)", "Cognitive Explorer", "⚡", 2250, "Gold League", isUser = true),
                    LeaderboardPlayerEntity(9, 9, "Aria_Zen", "Focus Knight", "💎", 2120, "Silver League"),
                    LeaderboardPlayerEntity(10, 10, "ZenMaster_X", "Acuity Seeker", "✨", 1980, "Silver League"),
                    LeaderboardPlayerEntity(11, 11, "ChronoCoder", "Apprentice", "🔮", 1750, "Silver League"),
                    LeaderboardPlayerEntity(12, 12, "Leo_Puzzler", "Novice Thinker", "🍀", 1540, "Bronze League")
                )
            )
        }
    }
}
