package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AppDatabase
import com.example.data.db.DailyChallengeCycleEntity
import com.example.data.db.DailyChallengeEntity
import com.example.data.db.UserStatsEntity
import com.example.ui.components.formatNumber
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GemCyan
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun DailyChallengesScreen(
    stats: UserStatsEntity?,
    cycle: DailyChallengeCycleEntity?,
    challenges: List<DailyChallengeEntity>,
    onClaimChallenge: (String) -> Unit,
    onClaimSetBonus: (String) -> Unit,
    onClaimStreak: (day: Int, coins: Int, gems: Int) -> Unit
) {
    val currentStreak = stats?.currentStreakDays ?: 1
    val lastClaimedDay = stats?.lastStreakClaimDay ?: 0
    val canClaimToday = currentStreak > lastClaimedDay

    // Live countdown timer for the 24-hour cycle
    var remainingMillis by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            val (_, endTime) = AppDatabase.getTodayCycleTimes()
            val now = System.currentTimeMillis()
            remainingMillis = (endTime - now).coerceAtLeast(0L)
            delay(1000)
        }
    }

    val countdownFormatted = remember(remainingMillis) {
        val totalSec = remainingMillis / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        String.format(Locale.US, "%02dh %02dm %02ds", h, m, s)
    }

    val completedCount = challenges.count { it.isCompleted }
    val totalCount = challenges.size
    val isSetCompleted = cycle?.isSetCompleted == true || (totalCount > 0 && completedCount >= totalCount)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvas)
            .padding(horizontal = 16.dp)
            .testTag("daily_challenges_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // 24-Hour Cycle Status Banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSetCompleted) DarkSurfaceElevated else DarkSurface
                ),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSetCompleted) NeonGreen else NeonCyan
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("24h_cycle_status_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSetCompleted) NeonGreen.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSetCompleted) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                                    contentDescription = "24h Cycle",
                                    tint = if (isSetCompleted) NeonGreen else NeonCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isSetCompleted) "24H SET COMPLETED!" else "DAILY 24H CYCLE",
                                    color = if (isSetCompleted) NeonGreen else TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (isSetCompleted) "1 of 1 set completed today" else "1 set available per 24-hour cycle",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            color = DarkSurfaceElevated,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Reset Timer",
                                    tint = NeonAmber,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = countdownFormatted,
                                    color = NeonAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Set Progress: $completedCount of $totalCount challenges completed",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isSetCompleted) "100%" else "${if (totalCount > 0) (completedCount * 100 / totalCount) else 0}%",
                            color = if (isSetCompleted) NeonGreen else NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { if (totalCount > 0) completedCount.toFloat() / totalCount else 0f },
                        color = if (isSetCompleted) NeonGreen else NeonCyan,
                        trackColor = DarkSurfaceElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                    )

                    // Grand 24-Hour Set Completion Bonus Reward Card
                    if (isSetCompleted) {
                        Spacer(modifier = Modifier.height(14.dp))
                        val bonusClaimed = cycle?.bonusClaimed == true
                        val cycleDate = cycle?.cycleDate ?: AppDatabase.getTodayCycleDate()

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (bonusClaimed) DarkSurface else NeonGreen.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (bonusClaimed) DarkBorder else NeonGreen
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("24h_set_bonus_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = "Bonus",
                                            tint = GoldCoin,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "24-Hour Set Bonus",
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "+500 Coins • +20 Gems",
                                        color = GoldCoin,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                if (bonusClaimed) {
                                    Surface(
                                        color = DarkSurfaceElevated,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "BONUS CLAIMED ✓",
                                            color = NeonGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { onClaimSetBonus(cycleDate) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("claim_set_bonus_button")
                                    ) {
                                        Text(
                                            text = "Claim Bonus",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7-Day Streak Calendar Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("streak_calendar_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonAmber.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = NeonAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "$currentStreak DAY STREAK",
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Log in daily to claim escalating rewards",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (canClaimToday) {
                            Surface(
                                color = NeonGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "READY",
                                    color = NeonGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val streakDaysRewards = listOf(
                        Triple(1, 100, 0),
                        Triple(2, 200, 2),
                        Triple(3, 350, 5),
                        Triple(4, 500, 8),
                        Triple(5, 750, 10),
                        Triple(6, 1000, 15),
                        Triple(7, 2000, 30)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(streakDaysRewards) { (day, coins, gems) ->
                            val isClaimed = day <= lastClaimedDay
                            val isToday = day == currentStreak
                            val isAvailable = isToday && canClaimToday

                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isAvailable -> DarkSurfaceElevated
                                        isClaimed -> DarkSurfaceElevated.copy(alpha = 0.5f)
                                        else -> DarkSurface
                                    }
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when {
                                        isAvailable -> NeonAmber
                                        isClaimed -> NeonGreen
                                        else -> DarkBorder
                                    }
                                ),
                                modifier = Modifier
                                    .width(76.dp)
                                    .testTag("streak_node_day_$day")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Day $day",
                                        color = if (isToday) NeonAmber else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isClaimed -> NeonGreen.copy(alpha = 0.2f)
                                                    isAvailable -> NeonAmber.copy(alpha = 0.2f)
                                                    else -> DarkSurfaceElevated
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isClaimed) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Claimed",
                                                tint = NeonGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else if (day == 7) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Grand",
                                                tint = GoldCoin,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = if (gems > 0) Icons.Default.Diamond else Icons.Default.MonetizationOn,
                                                contentDescription = "Reward",
                                                tint = if (gems > 0) GemCyan else GoldCoin,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "+$coins",
                                        color = GoldCoin,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (canClaimToday) {
                        val todayReward = streakDaysRewards.firstOrNull { it.first == currentStreak } ?: Triple(currentStreak, 200, 5)
                        Button(
                            onClick = {
                                onClaimStreak(todayReward.first, todayReward.second, todayReward.third)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("claim_today_streak_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = "Claim",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Claim Day $currentStreak (+${todayReward.second} Coins)",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Daily Quests
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S CHALLENGE SET",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isSetCompleted) "Next set in $countdownFormatted" else "Expires in $countdownFormatted",
                    color = if (isSetCompleted) NeonGreen else TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Challenge Items
        items(challenges) { challenge ->
            val isFinished = challenge.isCompleted || challenge.currentProgress >= challenge.targetValue

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isFinished && !challenge.isClaimed) NeonGreen else DarkBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("challenge_item_${challenge.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = challenge.title,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Reward Tag
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                tint = GoldCoin,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "+${challenge.rewardCoins}",
                                color = GoldCoin,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (challenge.rewardGems > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "Gems",
                                    tint = GemCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "+${challenge.rewardGems}",
                                    color = GemCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = challenge.description,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Bar
                    val progressFraction = if (challenge.targetValue > 0) {
                        (challenge.currentProgress.toFloat() / challenge.targetValue).coerceIn(0f, 1f)
                    } else 0f

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = if (isFinished) NeonGreen else NeonCyan,
                        trackColor = DarkSurfaceElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${challenge.currentProgress} / ${challenge.targetValue}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (challenge.isClaimed) {
                            Surface(
                                color = DarkSurfaceElevated,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "CLAIMED ✓",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else if (isFinished) {
                            Button(
                                onClick = { onClaimChallenge(challenge.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("claim_challenge_${challenge.id}")
                            ) {
                                Text(
                                    text = "Claim Reward",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        } else {
                            Text(
                                text = "In Progress",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
