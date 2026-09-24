package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.DailyChallengeCycleEntity
import com.example.data.db.DailyChallengeEntity
import com.example.data.repository.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

data class DailyChallengeUiState(
    val cycleDate: String = "",
    val cycleStartTimeMillis: Long = 0L,
    val cycleEndTimeMillis: Long = 0L,
    val timeRemainingMillis: Long = 0L,
    val formattedTimeRemaining: String = "24h 00m 00s",
    val isSetCompleted: Boolean = false,
    val completedAt: Long? = null,
    val allRewardsClaimed: Boolean = false,
    val bonusClaimed: Boolean = false,
    val bonusCoins: Int = 500,
    val bonusGems: Int = 20,
    val challenges: List<DailyChallengeEntity> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val canClaimBonus: Boolean = false,
    val isLockedForToday: Boolean = false,
    val isLoading: Boolean = true
)

class DailyChallengeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    private val appDatabase = AppDatabase.getInstance(application)

    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining: StateFlow<Long> = _timeRemaining.asStateFlow()

    val uiState: StateFlow<DailyChallengeUiState>

    init {
        repository = GameRepository(appDatabase.appDao())

        val cycleFlow = repository.getActiveCycle()
        val challengesFlow = repository.getChallengesForActiveCycle()

        uiState = combine(cycleFlow, challengesFlow, _timeRemaining) { cycle, challenges, remainingMs ->
            if (cycle == null) {
                DailyChallengeUiState(isLoading = true)
            } else {
                val completedCount = challenges.count { it.isCompleted }
                val totalCount = challenges.size
                val isSetComplete = cycle.isSetCompleted || (totalCount > 0 && completedCount >= totalCount)
                val canClaimBonus = isSetComplete && !cycle.bonusClaimed

                DailyChallengeUiState(
                    cycleDate = cycle.cycleDate,
                    cycleStartTimeMillis = cycle.cycleStartTimeMillis,
                    cycleEndTimeMillis = cycle.cycleEndTimeMillis,
                    timeRemainingMillis = remainingMs,
                    formattedTimeRemaining = formatMillisToCountdown(remainingMs),
                    isSetCompleted = isSetComplete,
                    completedAt = cycle.completedAt,
                    allRewardsClaimed = cycle.allRewardsClaimed,
                    bonusClaimed = cycle.bonusClaimed,
                    bonusCoins = cycle.bonusCoins,
                    bonusGems = cycle.bonusGems,
                    challenges = challenges,
                    completedCount = completedCount,
                    totalCount = totalCount,
                    canClaimBonus = canClaimBonus,
                    isLockedForToday = isSetComplete,
                    isLoading = false
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyChallengeUiState()
        )

        // Initialize and start 24-hour cycle countdown timer
        viewModelScope.launch {
            repository.ensureInitialized()
            startCountdownTimer()
        }
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (isActive) {
                val (_, endTime) = AppDatabase.getTodayCycleTimes()
                val now = System.currentTimeMillis()
                val remaining = (endTime - now).coerceAtLeast(0L)
                _timeRemaining.value = remaining

                // If cycle reached 0, auto-refresh to advance to new 24h cycle
                if (remaining == 0L) {
                    repository.checkAndCreateTodayCycle()
                }

                delay(1000)
            }
        }
    }

    fun refreshCycle() {
        viewModelScope.launch {
            repository.checkAndCreateTodayCycle()
        }
    }

    fun claimChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.claimChallengeReward(challengeId)
        }
    }

    fun claimSetBonus() {
        val currentCycleDate = uiState.value.cycleDate
        if (currentCycleDate.isNotBlank()) {
            viewModelScope.launch {
                repository.claimSetCompletionBonus(currentCycleDate)
            }
        }
    }

    private fun formatMillisToCountdown(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02dh %02dm %02ds", hours, minutes, seconds)
    }
}
