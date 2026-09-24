package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDao
import com.example.data.db.AppDatabase
import com.example.data.db.DailyChallengeCycleEntity
import com.example.data.db.DailyChallengeEntity
import com.example.data.db.UserStatsEntity
import com.example.data.model.GameMode
import com.example.data.model.GameResult
import com.example.data.repository.GameRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DailyChallengeDatabaseAndViewModelTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppDao
    private lateinit var repository: GameRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.appDao()
        repository = GameRepository(dao)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testRoomSchema_insertAndRetrieveCycleAndChallenges() = runTest {
        val cycleDate = "2026-09-24"
        val cycle = DailyChallengeCycleEntity(
            cycleDate = cycleDate,
            cycleStartTimeMillis = 1000L,
            cycleEndTimeMillis = 86400000L,
            isSetCompleted = false,
            bonusCoins = 500,
            bonusGems = 20
        )
        dao.insertCycle(cycle)

        val retrievedCycle = dao.getCycleDirect(cycleDate)
        assertNotNull(retrievedCycle)
        assertEquals(cycleDate, retrievedCycle?.cycleDate)
        assertFalse(retrievedCycle!!.isSetCompleted)

        val challenge = DailyChallengeEntity(
            id = "${cycleDate}_1",
            cycleDate = cycleDate,
            challengeIndex = 1,
            challengeType = "SPEED_MATH",
            title = "Speed Calculation",
            description = "Score over 1,000 pts",
            targetValue = 1000,
            currentProgress = 0,
            rewardCoins = 150,
            rewardGems = 5,
            isCompleted = false,
            isClaimed = false
        )
        dao.insertChallenges(listOf(challenge))

        val retrievedChallenges = dao.getChallengesForCycleDirect(cycleDate)
        assertEquals(1, retrievedChallenges.size)
        assertEquals("${cycleDate}_1", retrievedChallenges[0].id)
    }

    @Test
    fun test24HourCycleEnforcement_onlyOneSetCompletedPerCycle() = runTest {
        dao.insertUserStats(UserStatsEntity())
        val today = AppDatabase.getTodayCycleDate()
        val cycle = repository.checkAndCreateTodayCycle()

        assertEquals(today, cycle.cycleDate)
        assertFalse(cycle.isSetCompleted)

        // Complete the speed math challenge with a great score
        repository.recordGameResult(
            GameResult(
                mode = GameMode.SPEED_MATH,
                finalScore = 1200,
                correctCount = 10,
                wrongCount = 0,
                maxCombo = 5,
                coinsEarned = 100,
                gemsEarned = 5,
                xpGained = 120
            )
        )

        // Complete the word blitz challenge
        repository.recordGameResult(
            GameResult(
                mode = GameMode.WORD_BLITZ,
                finalScore = 1500,
                correctCount = 10,
                wrongCount = 0,
                maxCombo = 6,
                coinsEarned = 100,
                gemsEarned = 5,
                xpGained = 150
            )
        )

        // All challenges in the 24h cycle set should now be completed, marking the cycle as completed!
        val activeCycle = dao.getCycleDirect(today)
        assertNotNull(activeCycle)
        assertTrue("Cycle should be marked as completed", activeCycle!!.isSetCompleted)
        assertNotNull(activeCycle.completedAt)

        // Claim bonus for completing the set
        val bonusSuccess = repository.claimSetCompletionBonus(today)
        assertTrue("Bonus claiming should succeed", bonusSuccess)

        val updatedCycle = dao.getCycleDirect(today)
        assertTrue("Bonus claimed flag should be true", updatedCycle!!.bonusClaimed)

        val userStats = dao.getUserStats().first()
        assertNotNull(userStats)
        assertTrue("User should receive bonus coins", userStats!!.coins >= 500)
    }
}
