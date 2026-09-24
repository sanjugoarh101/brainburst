package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStatsEntity)

    @Update
    suspend fun updateUserStats(stats: UserStatsEntity)

    // Daily Challenge Cycles (24-Hour Cycle Management)
    @Query("SELECT * FROM daily_challenge_cycles WHERE cycleDate = :cycleDate LIMIT 1")
    fun getCycle(cycleDate: String): Flow<DailyChallengeCycleEntity?>

    @Query("SELECT * FROM daily_challenge_cycles WHERE cycleDate = :cycleDate LIMIT 1")
    suspend fun getCycleDirect(cycleDate: String): DailyChallengeCycleEntity?

    @Query("SELECT * FROM daily_challenge_cycles ORDER BY cycleStartTimeMillis DESC LIMIT 1")
    fun getLatestCycle(): Flow<DailyChallengeCycleEntity?>

    @Query("SELECT * FROM daily_challenge_cycles ORDER BY cycleStartTimeMillis DESC LIMIT 1")
    suspend fun getLatestCycleDirect(): DailyChallengeCycleEntity?

    @Query("SELECT * FROM daily_challenge_cycles WHERE isSetCompleted = 1 ORDER BY cycleStartTimeMillis DESC")
    fun getCompletedCyclesHistory(): Flow<List<DailyChallengeCycleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: DailyChallengeCycleEntity)

    @Update
    suspend fun updateCycle(cycle: DailyChallengeCycleEntity)

    // Daily Challenges (Scoped by Cycle)
    @Query("SELECT * FROM daily_challenges WHERE cycleDate = :cycleDate ORDER BY challengeIndex ASC")
    fun getChallengesForCycle(cycleDate: String): Flow<List<DailyChallengeEntity>>

    @Query("SELECT * FROM daily_challenges WHERE cycleDate = :cycleDate ORDER BY challengeIndex ASC")
    suspend fun getChallengesForCycleDirect(cycleDate: String): List<DailyChallengeEntity>

    @Query("SELECT * FROM daily_challenges WHERE id = :id LIMIT 1")
    suspend fun getChallengeById(id: String): DailyChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<DailyChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: DailyChallengeEntity)

    @Query("SELECT COUNT(*) FROM daily_challenges WHERE cycleDate = :cycleDate AND isCompleted = 1")
    suspend fun countCompletedChallengesInCycle(cycleDate: String): Int

    @Query("SELECT COUNT(*) FROM daily_challenges WHERE cycleDate = :cycleDate")
    suspend fun countTotalChallengesInCycle(cycleDate: String): Int

    // Leaderboards
    @Query("SELECT * FROM leaderboard_players ORDER BY rank ASC")
    fun getLeaderboard(): Flow<List<LeaderboardPlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboard(players: List<LeaderboardPlayerEntity>)

    @Query("UPDATE leaderboard_players SET score = :newScore, rank = :newRank WHERE isUser = 1")
    suspend fun updateUserLeaderboardScore(newScore: Int, newRank: Int)
}
