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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.db.UserStatsEntity
import com.example.data.model.MicrotransactionItem
import com.example.data.model.PowerUpType
import com.example.ui.components.MicrotransactionPurchaseModal
import com.example.ui.components.SimulatedAdRewardModal
import com.example.ui.components.formatNumber
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GameThemePreset
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
fun ShopScreen(
    stats: UserStatsEntity?,
    onBuyPowerUp: (PowerUpType) -> Unit,
    onPurchaseMicrotransaction: (MicrotransactionItem) -> Unit,
    onClaimAdReward: () -> Unit,
    onEquipTheme: (GameThemePreset) -> Unit
) {
    var selectedPackToBuy by remember { mutableStateOf<MicrotransactionItem?>(null) }
    var showAdModal by remember { mutableStateOf(false) }

    val unlockedThemesList = stats?.unlockedThemes?.split(",")?.map { it.trim() } ?: listOf("cyber_violet")

    val bundles = listOf(
        MicrotransactionItem(
            id = "starter_kit",
            title = "Starter Brain Kit",
            description = "Boost your game with coins, gems and powers",
            priceUsd = "$0.99",
            coinsAmount = 5000,
            gemsAmount = 50,
            isBundle = true,
            badgeLabel = "BEST VALUE • 75% OFF"
        ),
        MicrotransactionItem(
            id = "pro_vault",
            title = "Pro Cognitive Vault",
            description = "Massive reserves for serious climbers",
            priceUsd = "$2.99",
            coinsAmount = 20000,
            gemsAmount = 250,
            isBundle = true,
            badgeLabel = "POPULAR"
        ),
        MicrotransactionItem(
            id = "grandmaster_bundle",
            title = "Grandmaster Mind Empire",
            description = "Ultimate package with all powers and prestige",
            priceUsd = "$4.99",
            coinsAmount = 60000,
            gemsAmount = 800,
            isBundle = true,
            badgeLabel = "EPIC"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCanvas)
            .padding(horizontal = 16.dp)
            .testTag("shop_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Free Sponsor Daily Gift
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("free_sponsor_gift_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NeonGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Free Boost",
                                tint = NeonGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Surface(
                                color = NeonGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "100% FREE REWARD",
                                    color = NeonGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Daily Sponsor Boost",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "+200 Coins & +5 Gems",
                                    color = GoldCoin,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { showAdModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("claim_free_boost_button")
                    ) {
                        Text(
                            text = "Claim",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Section: Optional Microtransactions (In-App Store)
        item {
            Column {
                Text(
                    text = "OPTIONAL MICROTRANSACTIONS",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Optional bundles to accelerate progress (Free to Play friendly)",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Bundle Cards
        items(bundles) { bundle ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPackToBuy = bundle }
                    .testTag("bundle_card_${bundle.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (bundle.badgeLabel != null) {
                            Surface(
                                color = NeonAmber,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = bundle.badgeLabel,
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Text(
                            text = bundle.priceUsd,
                            color = NeonCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = bundle.title,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = bundle.description,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                tint = GoldCoin,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${formatNumber(bundle.coinsAmount)}",
                                color = GoldCoin,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
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
                                text = "+${formatNumber(bundle.gemsAmount)}",
                                color = GemCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = { selectedPackToBuy = bundle },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = "Buy ${bundle.priceUsd}",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Section: Power-Ups (In-Game Coins Shop)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                Text(
                    text = "BOOSTERS & POWER-UPS",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Use your free earned coins to purchase gameplay perks",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        items(PowerUpType.values()) { powerUp ->
            val userCoins = stats?.coins ?: 0
            val canAfford = userCoins >= powerUp.coinCost
            val icon = when (powerUp) {
                PowerUpType.FREEZE_TIME -> Icons.Default.AcUnit
                PowerUpType.HINT_5050 -> Icons.Default.HelpOutline
                PowerUpType.SHIELD -> Icons.Default.Shield
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("powerup_shop_${powerUp.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceElevated)
                                .border(1.dp, NeonViolet, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = powerUp.displayName,
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = powerUp.displayName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = powerUp.description,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = { onBuyPowerUp(powerUp) },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) GoldCoin else DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("buy_powerup_${powerUp.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                tint = if (canAfford) Color.Black else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${powerUp.coinCost}",
                                color = if (canAfford) Color.Black else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Section: Cosmetic Themes
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                Text(
                    text = "COSMETIC GAME THEMES",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Customize the app aesthetic and colors",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        items(GameThemePreset.values()) { themePreset ->
            val isUnlocked = unlockedThemesList.contains(themePreset.id)
            val isCurrent = stats?.activeThemeId == themePreset.id

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCurrent) NeonCyan else DarkBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEquipTheme(themePreset) }
                    .testTag("theme_card_${themePreset.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(themePreset.primary, themePreset.secondary)
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = themePreset.displayName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isCurrent) "Equipped & Active" else if (isUnlocked) "Unlocked" else "Cosmetic Unlock",
                                color = if (isCurrent) NeonCyan else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (isCurrent) {
                        Surface(
                            color = NeonCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else if (isUnlocked) {
                        Button(
                            onClick = { onEquipTheme(themePreset) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(text = "Equip", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { onEquipTheme(themePreset) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldCoin),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(text = "1,000 Coins", color = GoldCoin, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Google Play Modal Simulation
    MicrotransactionPurchaseModal(
        item = selectedPackToBuy,
        onDismiss = { selectedPackToBuy = null },
        onConfirmPurchase = { item ->
            onPurchaseMicrotransaction(item)
        }
    )

    // Ad Reward Modal
    if (showAdModal) {
        SimulatedAdRewardModal(
            onRewardEarned = {
                onClaimAdReward()
                showAdModal = false
            },
            onDismiss = { showAdModal = false }
        )
    }
}
