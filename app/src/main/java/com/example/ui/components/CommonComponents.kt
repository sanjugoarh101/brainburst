package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MicrotransactionItem
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GemCyan
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun BrainBurstTopBar(
    avatarEmoji: String,
    level: Int,
    coins: Int,
    gems: Int,
    streakDays: Int,
    onShopClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        color = DarkSurface,
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player Profile badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onProfileClick() }
                    .padding(4.dp)
                    .testTag("topbar_profile_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NeonViolet, NeonPink)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = avatarEmoji, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "LVL $level",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = NeonAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "$streakDays d",
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Currency Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Coins
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, GoldCoinDark.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { onShopClick() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("topbar_coins_badge")
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = GoldCoin,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = formatNumber(coins),
                        color = GoldCoin,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Gems
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, GemCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { onShopClick() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("topbar_gems_badge")
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Gems",
                        tint = GemCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = formatNumber(gems),
                        color = GemCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

fun formatNumber(num: Int): String {
    return when {
        num >= 1_000_000 -> String.format("%.1fM", num / 1_000_000f)
        num >= 10_000 -> String.format("%.1fk", num / 1_000f)
        else -> "%,d".format(num)
    }
}

@Composable
fun ComboFeverBanner(
    combo: Int,
    multiplier: Int,
    isFever: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fever_scale"
    )

    val currentScale = if (isFever) scale else 1f
    val bgColors = if (isFever) {
        listOf(NeonPink, NeonViolet, NeonCyan)
    } else if (multiplier > 1) {
        listOf(NeonViolet, NeonPurple)
    } else {
        listOf(DarkSurfaceElevated, DarkBorder)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(currentScale)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(bgColors))
            .border(
                width = if (isFever) 2.dp else 1.dp,
                color = if (isFever) NeonCyan else DarkBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isFever) Icons.Default.Bolt else Icons.Default.Star,
                    contentDescription = "Combo",
                    tint = if (isFever) GoldCoin else NeonCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFever) "FEVER MODE ACTIVE!" else "COMBO STREAK",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${combo}x",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = if (isFever) GoldCoin else NeonCyan,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${multiplier}X SCORE",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// Particle Confetti Effect
private data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var speedX: Float,
    var speedY: Float,
    var rotation: Float,
    var rotationSpeed: Float,
    val color: Color,
    val size: Float
)

@Composable
fun ConfettiOverlay(
    active: Boolean,
    onFinished: () -> Unit = {}
) {
    if (!active) return

    var particles by remember {
        mutableStateOf(
            List(45) {
                val rand = Random.Default
                ConfettiParticle(
                    x = rand.nextFloat() * 1000f,
                    y = -rand.nextFloat() * 200f,
                    speedX = (rand.nextFloat() - 0.5f) * 8f,
                    speedY = rand.nextFloat() * 14f + 8f,
                    rotation = rand.nextFloat() * 360f,
                    rotationSpeed = (rand.nextFloat() - 0.5f) * 12f,
                    color = listOf(GoldCoin, NeonCyan, NeonPink, NeonViolet, NeonGreen)[rand.nextInt(5)],
                    size = rand.nextFloat() * 14f + 8f
                )
            }
        )
    }

    LaunchedEffect(active) {
        var elapsed = 0
        while (elapsed < 2400) {
            delay(16)
            elapsed += 16
            particles = particles.map { p ->
                p.copy(
                    x = p.x + p.speedX,
                    y = p.y + p.speedY,
                    rotation = p.rotation + p.rotationSpeed
                )
            }
        }
        onFinished()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            rotate(p.rotation, pivot = Offset(p.x, p.y)) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(p.x, p.y),
                    size = Size(p.size, p.size * 0.6f)
                )
            }
        }
    }
}

@Composable
fun MicrotransactionPurchaseModal(
    item: MicrotransactionItem?,
    onDismiss: () -> Unit,
    onConfirmPurchase: (MicrotransactionItem) -> Unit
) {
    if (item == null) return

    var isProcessing by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("purchase_modal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Google Play Purchase",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(
                        onClick = { if (!isProcessing) onDismiss() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Item Badge Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(NeonViolet, DarkSurface)
                            )
                        )
                        .border(2.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.gemsAmount > 0) Icons.Default.Diamond else Icons.Default.MonetizationOn,
                        contentDescription = "Pack",
                        tint = if (item.gemsAmount > 0) GemCyan else GoldCoin,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = item.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Rewards pill
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.coinsAmount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Coins",
                                    tint = GoldCoin,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+${formatNumber(item.coinsAmount)} Coins",
                                    color = GoldCoin,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        if (item.gemsAmount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "Gems",
                                    tint = GemCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+${formatNumber(item.gemsAmount)} Gems",
                                    color = GemCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Price and 1-Tap Buy button
                Button(
                    onClick = {
                        isProcessing = true
                    },
                    enabled = !isProcessing,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("one_tap_buy_button")
                ) {
                    if (isProcessing) {
                        Text(
                            text = "Authorizing...",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Secure",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1-Tap Buy • ${item.priceUsd}",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                LaunchedEffect(isProcessing) {
                    if (isProcessing) {
                        delay(900)
                        onConfirmPurchase(item)
                        isProcessing = false
                        onDismiss()
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Free to play simulation • No real payment charged",
                    color = TextSecondary.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun SimulatedAdRewardModal(
    onRewardEarned: () -> Unit,
    onDismiss: () -> Unit
) {
    var secondsLeft by remember { mutableIntStateOf(4) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        delay(300)
        onRewardEarned()
    }

    Dialog(onDismissRequest = { if (secondsLeft == 0) onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
            modifier = Modifier.fillMaxWidth().testTag("ad_reward_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SPONSOR BONUS GIFT",
                    color = NeonCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .border(2.dp, NeonAmber, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (secondsLeft > 0) "$secondsLeft" else "✓",
                            color = NeonAmber,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (secondsLeft > 0) {
                            Text(
                                text = "sec",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (secondsLeft > 0) "Generating daily sponsor boost..." else "Reward Unlocked!",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = GoldCoin,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+200 Coins",
                        color = GoldCoin,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Gems",
                        tint = GemCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+5 Gems",
                        color = GemCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    enabled = secondsLeft == 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    modifier = Modifier.fillMaxWidth().testTag("claim_ad_reward_button")
                ) {
                    Text(
                        text = if (secondsLeft > 0) "Please wait..." else "Claim & Return",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
