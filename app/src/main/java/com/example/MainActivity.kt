package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BrainBurstTopBar
import com.example.ui.screens.ActiveGameScreen
import com.example.ui.screens.DailyChallengesScreen
import com.example.ui.screens.LeaderboardsScreen
import com.example.ui.screens.PlayHubScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.BrainBurstTheme
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.HapticsManager
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val haptics = remember { HapticsManager(context) }
            val viewModel: GameViewModel = viewModel()

            val stats by viewModel.userStats.collectAsStateWithLifecycle()
            val challenges by viewModel.dailyChallenges.collectAsStateWithLifecycle()
            val activeCycle by viewModel.activeCycle.collectAsStateWithLifecycle()
            val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val activeGameMode by viewModel.activeGameMode.collectAsStateWithLifecycle()
            val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()

            BrainBurstTheme(activePreset = activeTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkCanvas)
                ) {
                    if (activeGameMode != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(WindowInsets.statusBars)
                                .windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            ActiveGameScreen(
                                mode = activeGameMode!!,
                                freezeCount = stats?.freezeCount ?: 0,
                                hintCount = stats?.hintCount ?: 0,
                                shieldCount = stats?.shieldCount ?: 0,
                                haptics = haptics,
                                onUsePowerUp = { powerUp -> viewModel.usePowerUp(powerUp) },
                                onGameFinished = { result -> viewModel.finishGame(result) },
                                onQuitGame = { viewModel.quitGame() }
                            )
                        }
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = DarkCanvas,
                            topBar = {
                                Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                                    BrainBurstTopBar(
                                        avatarEmoji = stats?.avatarEmoji ?: "⚡",
                                        level = stats?.level ?: 1,
                                        coins = stats?.coins ?: 0,
                                        gems = stats?.gems ?: 0,
                                        streakDays = stats?.currentStreakDays ?: 1,
                                        onShopClick = { viewModel.selectTab(NavigationTab.SHOP) },
                                        onProfileClick = { viewModel.selectTab(NavigationTab.PROFILE) }
                                    )
                                }
                            },
                            bottomBar = {
                                NavigationBar(
                                    containerColor = DarkSurface,
                                    modifier = Modifier
                                        .border(1.dp, DarkBorder)
                                        .testTag("main_navigation_bar"),
                                    windowInsets = WindowInsets.navigationBars
                                ) {
                                    val items = listOf(
                                        Triple(NavigationTab.HUB, Icons.Filled.PlayArrow, Icons.Outlined.PlayArrow),
                                        Triple(NavigationTab.CHALLENGES, Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard),
                                        Triple(NavigationTab.LEADERBOARDS, Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
                                        Triple(NavigationTab.SHOP, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
                                        Triple(NavigationTab.PROFILE, Icons.Filled.Person, Icons.Outlined.Person)
                                    )

                                    items.forEach { (tab, filledIcon, outlinedIcon) ->
                                        val isSelected = currentTab == tab
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = {
                                                haptics.tap()
                                                viewModel.selectTab(tab)
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = if (isSelected) filledIcon else outlinedIcon,
                                                    contentDescription = tab.title,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = tab.title,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = DarkCanvas,
                                                selectedTextColor = NeonCyan,
                                                indicatorColor = NeonCyan,
                                                unselectedIconColor = TextSecondary,
                                                unselectedTextColor = TextSecondary
                                            ),
                                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                AnimatedContent(
                                    targetState = currentTab,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "screen_transition"
                                ) { tab ->
                                    when (tab) {
                                        NavigationTab.HUB -> PlayHubScreen(
                                            stats = stats,
                                            onStartGame = { mode -> viewModel.startGame(mode) },
                                            onNavigateDailyChallenges = { viewModel.selectTab(NavigationTab.CHALLENGES) }
                                        )
                                        NavigationTab.CHALLENGES -> DailyChallengesScreen(
                                            stats = stats,
                                            cycle = activeCycle,
                                            challenges = challenges,
                                            onClaimChallenge = { id -> viewModel.claimDailyChallenge(id) },
                                            onClaimSetBonus = { cycleDate -> viewModel.claimSetBonus(cycleDate) },
                                            onClaimStreak = { day, coins, gems -> viewModel.claimStreak(day, coins, gems) }
                                        )
                                        NavigationTab.LEADERBOARDS -> LeaderboardsScreen(
                                            players = leaderboard,
                                            onChallengeRival = { rival -> viewModel.challengeRival(rival) }
                                        )
                                        NavigationTab.SHOP -> ShopScreen(
                                            stats = stats,
                                            onBuyPowerUp = { type -> viewModel.buyPowerUp(type) },
                                            onPurchaseMicrotransaction = { item -> viewModel.purchaseMicrotransaction(item) },
                                            onClaimAdReward = { viewModel.claimAdReward() },
                                            onEquipTheme = { theme -> viewModel.equipTheme(theme) }
                                        )
                                        NavigationTab.PROFILE -> ProfileScreen(
                                            stats = stats,
                                            onSelectAvatar = { emoji -> viewModel.selectAvatar(emoji) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "BrainBurst $name", modifier = modifier)
}
