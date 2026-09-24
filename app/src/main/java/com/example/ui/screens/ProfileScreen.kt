package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun ProfileScreen(
    stats: UserStatsEntity?,
    onSelectAvatar: (String) -> Unit
) {
    val level = stats?.level ?: 1
    val xp = stats?.xp ?: 0
    val xpNeeded = level * 300
    val xpProgress = (xp % 300).toFloat() / 300f

    val avatars = listOf("⚡", "🧠", "🌟", "👑", "🚀", "💎", "🎯", "🔥")

    val badges = listOf(
        Triple("Math Titan", "Scored 1,000+ in Speed Math", (stats?.mathHighScore ?: 0) >= 1000),
        Triple("Lexicon Wiz", "Scored 900+ in Word Blitz", (stats?.wordHighScore ?: 0) >= 900),
        Triple("Memory Matrix", "Scored 1,000+ in Memory Matrix", (stats?.memoryHighScore ?: 0) >= 1000),
        Triple("Fever Master", "Triggered Fever Mode", true),
        Triple("Streak Devotee", "Maintained 3+ day streak", (stats?.currentStreakDays ?: 0) >= 3),
        Triple("Workout King", "Completed Daily Mind Gym", (stats?.workoutHighScore ?: 0) >= 2000)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvas)
            .padding(horizontal = 16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Player Profile Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                modifier = Modifier.fillMaxWidth().testTag("profile_header_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(NeonViolet, NeonPink))
                            )
                            .border(2.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stats?.avatarEmoji ?: "⚡", fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stats?.username ?: "Brainiac",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    Surface(
                        color = NeonViolet.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = stats?.title ?: "Cognitive Explorer",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Level & XP Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Level $level",
                            color = NeonAmber,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${xp % 300} / 300 XP",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { xpProgress },
                        color = NeonAmber,
                        trackColor = DarkSurfaceElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }

        // Avatar Picker
        item {
            Column {
                Text(
                    text = "CHOOSE AVATAR",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(avatars) { emoji ->
                        val isSelected = stats?.avatarEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NeonViolet else DarkSurfaceElevated)
                                .border(
                                    2.dp,
                                    if (isSelected) NeonCyan else DarkBorder,
                                    CircleShape
                                )
                                .clickable { onSelectAvatar(emoji) }
                                .testTag("avatar_choice_$emoji"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                    }
                }
            }
        }

        // High Scores Showcase
        item {
            Column {
                Text(
                    text = "PERSONAL BEST RECORDS",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ScoreRecordRow("Speed Math", stats?.mathHighScore ?: 0, NeonPink)
                        ScoreRecordRow("Word Blitz", stats?.wordHighScore ?: 0, NeonCyan)
                        ScoreRecordRow("Memory Matrix", stats?.memoryHighScore ?: 0, NeonAmber)
                        ScoreRecordRow("Daily Workout", stats?.workoutHighScore ?: 0, NeonViolet)
                    }
                }
            }
        }

        // Badges & Achievements
        item {
            Column {
                Text(
                    text = "ACHIEVEMENTS & BADGES",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(badges) { (title, desc, isUnlocked) ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) DarkSurface else DarkSurface.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUnlocked) NeonGreen.copy(alpha = 0.5f) else DarkBorder
                ),
                modifier = Modifier.fillMaxWidth().testTag("badge_$title")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isUnlocked) NeonGreen.copy(alpha = 0.2f) else DarkSurfaceElevated
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isUnlocked) Icons.Default.MilitaryTech else Icons.Default.Star,
                            contentDescription = title,
                            tint = if (isUnlocked) NeonGreen else TextSecondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = if (isUnlocked) TextPrimary else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = desc,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    if (isUnlocked) {
                        Surface(
                            color = NeonGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "UNLOCKED",
                                color = NeonGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

@Composable
fun ScoreRecordRow(name: String, score: Int, tint: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(tint)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = name,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Points",
                tint = GoldCoin,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatNumber(score),
                color = GoldCoin,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
