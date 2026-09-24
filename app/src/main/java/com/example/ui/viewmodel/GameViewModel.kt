package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.DailyChallengeCycleEntity
import com.example.data.db.DailyChallengeEntity
import com.example.data.db.LeaderboardPlayerEntity
import com.example.data.db.UserStatsEntity
import com.example.data.model.GameMode
import com.example.data.model.GameResult
import com.example.data.model.MicrotransactionItem
import com.example.data.model.PowerUpType
import com.example.data.repository.GameRepository
import com.example.ui.theme.GameThemePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab(val title: String) {
    HUB("Play"),
    CHALLENGES("Daily Quests"),
    LEADERBOARDS("Ranks"),
    SHOP("Store"),
    PROFILE("Profile")
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository

    val userStats: StateFlow<UserStatsEntity?>
    val dailyChallenges: StateFlow<List<DailyChallengeEntity>>
    val activeCycle: StateFlow<DailyChallengeCycleEntity?>
    val leaderboard: StateFlow<List<LeaderboardPlayerEntity>>

    private val _currentTab = MutableStateFlow(NavigationTab.HUB)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _activeGameMode = MutableStateFlow<GameMode?>(null)
    val activeGameMode: StateFlow<GameMode?> = _activeGameMode.asStateFlow()

    private val _activeTheme = MutableStateFlow(GameThemePreset.CYBER_VIOLET)
    val activeTheme: StateFlow<GameThemePreset> = _activeTheme.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = GameRepository(database.appDao())

        userStats = repository.userStats.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStatsEntity()
        )

        dailyChallenges = repository.getChallengesForActiveCycle().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activeCycle = repository.getActiveCycle().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        leaderboard = repository.leaderboard.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.ensureInitialized()
        }

        viewModelScope.launch {
            userStats.collect { stats ->
                if (stats != null) {
                    val preset = GameThemePreset.values().find { it.id == stats.activeThemeId }
                        ?: GameThemePreset.CYBER_VIOLET
                    _activeTheme.value = preset
                }
            }
        }
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun startGame(mode: GameMode) {
        _activeGameMode.value = mode
    }

    fun quitGame() {
        _activeGameMode.value = null
    }

    fun finishGame(result: GameResult) {
        viewModelScope.launch {
            repository.recordGameResult(result)
            _activeGameMode.value = null
        }
    }

    fun usePowerUp(type: PowerUpType): Boolean {
        val stats = userStats.value ?: return false
        val hasInventory = when (type) {
            PowerUpType.FREEZE_TIME -> stats.freezeCount > 0
            PowerUpType.HINT_5050 -> stats.hintCount > 0
            PowerUpType.SHIELD -> stats.shieldCount > 0
        }
        if (hasInventory) {
            viewModelScope.launch {
                repository.consumePowerUp(type)
            }
            return true
        }
        return false
    }

    fun buyPowerUp(type: PowerUpType) {
        viewModelScope.launch {
            repository.buyPowerUp(type)
        }
    }

    fun claimDailyChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.claimChallengeReward(challengeId)
        }
    }

    fun claimSetBonus(cycleDate: String) {
        viewModelScope.launch {
            repository.claimSetCompletionBonus(cycleDate)
        }
    }

    fun claimStreak(day: Int, coins: Int, gems: Int) {
        viewModelScope.launch {
            repository.claimDailyStreak(day, coins, gems)
        }
    }

    fun purchaseMicrotransaction(item: MicrotransactionItem) {
        viewModelScope.launch {
            repository.applyMicrotransactionPurchase(item.coinsAmount, item.gemsAmount)
        }
    }

    fun claimAdReward() {
        viewModelScope.launch {
            repository.claimFreeAdGift(coinsBonus = 200, gemsBonus = 5)
        }
    }

    fun equipTheme(theme: GameThemePreset) {
        viewModelScope.launch {
            repository.unlockAndEquipTheme(theme.id, costCoins = 1000)
            _activeTheme.value = theme
        }
    }

    fun selectAvatar(emoji: String) {
        viewModelScope.launch {
            val stats = userStats.value ?: return@launch
            val db = AppDatabase.getInstance(getApplication())
            db.appDao().updateUserStats(stats.copy(avatarEmoji = emoji))
        }
    }

    fun challengeRival(rival: LeaderboardPlayerEntity) {
        startGame(GameMode.SPEED_MATH)
    }
}
