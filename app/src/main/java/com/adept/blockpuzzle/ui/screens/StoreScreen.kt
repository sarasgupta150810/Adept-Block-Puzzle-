package com.adept.blockpuzzle.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adept.blockpuzzle.model.BoosterType
import com.adept.blockpuzzle.model.GameTheme
import com.adept.blockpuzzle.model.StoreBoosterItem
import com.adept.blockpuzzle.model.StoreCatalog
import com.adept.blockpuzzle.ui.components.JuicyButton
import com.adept.blockpuzzle.ui.components.SkyHexBokehBackground
import com.adept.blockpuzzle.ui.theme.HexColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StoreScreen(
    coins: Int,
    hammersCount: Int,
    shufflesCount: Int,
    rainbowCount: Int,
    ownedThemes: Set<String>,
    activeThemeId: String,
    canClaimDailyReward: Boolean,
    canClaimAdReward: Boolean,
    rewardNotification: String?,
    onBuyBooster: (BoosterType, Int) -> Unit,
    onBuyTheme: (GameTheme) -> Unit,
    onEquipTheme: (String) -> Unit,
    onClaimDailyReward: () -> Unit,
    onClaimAdReward: () -> Unit,
    onDismissNotification: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    val coinPulse = remember { Animatable(1f) }
    LaunchedEffect(coins) {
        coinPulse.animateTo(1.15f, tween(120))
        coinPulse.animateTo(1f, tween(180))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("store_screen")
    ) {
        // High quality background
        SkyHexBokehBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // TOP HEADER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(2.dp, Color(0xFF38BDF8), CircleShape)
                        .clickable { onBack() }
                        .testTag("store_back_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Text(
                    text = "MAGIC STORE",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B),
                    letterSpacing = 1.5.sp
                )

                // COIN PILL BADGE
                CoinBalancePill(
                    coins = coins,
                    formatter = formatter,
                    scale = coinPulse.value
                )
            }

            // NOTIFICATION BANNER (if active)
            AnimatedVisibility(
                visible = rewardNotification != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (rewardNotification != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0F766E), Color(0xFF047857))
                                )
                            )
                            .border(1.5.dp, Color(0xFF34D399), RoundedCornerShape(12.dp))
                            .clickable { onDismissNotification() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("store_notification_banner"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rewardNotification,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // CATEGORY TABS
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0x331E293B),
                contentColor = Color(0xFF0284C7),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF0284C7),
                        height = 3.5.dp
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "⚡ BOOSTERS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (selectedTab == 0) Color(0xFF0F172A) else Color(0xFF475569)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "🎨 THEMES",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (selectedTab == 1) Color(0xFF0F172A) else Color(0xFF475569)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "🎁 FREE GIFTS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (selectedTab == 2) Color(0xFF0F172A) else Color(0xFF475569)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TAB CONTENT
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // BOOSTERS
                        items(StoreCatalog.BOOSTERS.size) { index ->
                            val item = StoreCatalog.BOOSTERS[index]
                            val ownedCount = when (item.type) {
                                BoosterType.HAMMER -> hammersCount
                                BoosterType.SHUFFLE -> shufflesCount
                                BoosterType.RAINBOW -> rainbowCount
                            }
                            BoosterStoreCard(
                                item = item,
                                ownedCount = ownedCount,
                                canAfford = coins >= item.price,
                                onBuy = { onBuyBooster(item.type, item.price) }
                            )
                        }
                    }

                    1 -> {
                        // THEMES
                        items(GameTheme.ALL_THEMES.size) { index ->
                            val theme = GameTheme.ALL_THEMES[index]
                            val isOwned = ownedThemes.contains(theme.id) || theme.price == 0
                            val isEquipped = activeThemeId == theme.id

                            ThemeStoreCard(
                                theme = theme,
                                isOwned = isOwned,
                                isEquipped = isEquipped,
                                canAfford = coins >= theme.price,
                                onEquip = { onEquipTheme(theme.id) },
                                onBuy = { onBuyTheme(theme) }
                            )
                        }
                    }

                    2 -> {
                        // FREE GIFTS & REWARDS
                        item {
                            FreeRewardCard(
                                title = "DAILY MYSTERY GIFT",
                                description = "Claim your daily gift: 250 Coins + 1 Free Hammer every 24 hours!",
                                emoji = "🎁",
                                rewardText = "+250 🪙 & +1 🔨",
                                isReady = canClaimDailyReward,
                                readyButtonText = "CLAIM FREE GIFT",
                                waitingText = "CLAIMED FOR TODAY ✓",
                                onClaim = onClaimDailyReward
                            )
                        }

                        item {
                            FreeRewardCard(
                                title = "LUCKY BONUS CHEST",
                                description = "Open the lucky chest to receive 100 free coins instantly! (Refreshes every 5m)",
                                emoji = "💎",
                                rewardText = "+100 🪙",
                                isReady = canClaimAdReward,
                                readyButtonText = "OPEN CHEST",
                                waitingText = "RECHARGING (5m) ⏳",
                                onClaim = onClaimAdReward
                            )
                        }

                        item {
                            StarterBonusCard(
                                coins = coins,
                                onAddStarterPack = onClaimAdReward
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoinBalancePill(
    coins: Int,
    formatter: NumberFormat,
    scale: Float = 1f
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF24324D), Color(0xFF131D31))
                )
            )
            .border(2.dp, Color(0xFFFBBF24), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("coin_balance_pill"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "🪙",
                fontSize = 16.sp
            )
            Text(
                text = formatter.format(coins),
                color = Color(0xFFFDE047),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun BoosterStoreCard(
    item: StoreBoosterItem,
    ownedCount: Int,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9))
                )
            )
            .border(2.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("booster_card_${item.type.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                        )
                    )
                    .border(2.dp, Color.White, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.iconEmoji,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    // Owned count badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E8F0))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Owned: $ownedCount",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Buy Button
            JuicyButton(
                text = "${item.price} 🪙",
                topColor = if (canAfford) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                bottomColor = if (canAfford) Color(0xFFD97706) else Color(0xFF64748B),
                borderColor = if (canAfford) Color(0xFFB45309) else Color(0xFF475569),
                onClick = onBuy,
                enabled = canAfford,
                modifier = Modifier
                    .width(96.dp)
                    .height(46.dp)
                    .testTag("buy_booster_${item.type.name.lowercase()}")
            )
        }
    }
}

@Composable
fun ThemeStoreCard(
    theme: GameTheme,
    isOwned: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onEquip: () -> Unit,
    onBuy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9))
                )
            )
            .border(
                width = if (isEquipped) 3.5.dp else 2.5.dp,
                color = if (isEquipped) Color(0xFF0284C7) else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
            .testTag("theme_card_${theme.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme Visual Palette Preview
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(theme.bgTop, theme.boardBackdrop)
                        )
                    )
                    .border(2.5.dp, theme.boardBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = theme.iconEmoji,
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = theme.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    if (isEquipped) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0284C7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = theme.description,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action Button
            if (isEquipped) {
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Equipped",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "EQUIPPED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0284C7)
                        )
                    }
                }
            } else if (isOwned) {
                JuicyButton(
                    text = "EQUIP",
                    topColor = Color(0xFF38BDF8),
                    bottomColor = Color(0xFF0284C7),
                    borderColor = Color(0xFF0369A1),
                    onClick = onEquip,
                    modifier = Modifier
                        .width(96.dp)
                        .height(46.dp)
                        .testTag("equip_theme_${theme.id}")
                )
            } else {
                JuicyButton(
                    text = "${theme.price} 🪙",
                    topColor = if (canAfford) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                    bottomColor = if (canAfford) Color(0xFFD97706) else Color(0xFF64748B),
                    borderColor = if (canAfford) Color(0xFFB45309) else Color(0xFF475569),
                    onClick = onBuy,
                    enabled = canAfford,
                    modifier = Modifier
                        .width(96.dp)
                        .height(46.dp)
                        .testTag("buy_theme_${theme.id}")
                )
            }
        }
    }
}

@Composable
fun FreeRewardCard(
    title: String,
    description: String,
    emoji: String,
    rewardText: String,
    isReady: Boolean,
    readyButtonText: String,
    waitingText: String,
    onClaim: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9))
                )
            )
            .border(2.5.dp, if (isReady) Color(0xFF22C55E) else Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("free_reward_${title.lowercase().replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF86EFAC), Color(0xFF22C55E))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = rewardText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF15803D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (isReady) {
                JuicyButton(
                    text = readyButtonText,
                    topColor = Color(0xFF8AE81B),
                    bottomColor = Color(0xFF4CA606),
                    borderColor = Color(0xFF2B6B00),
                    onClick = onClaim,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("claim_button_${title.lowercase().replace(" ", "_")}")
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = waitingText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
fun StarterBonusCard(
    coins: Int,
    onAddStarterPack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                )
            )
            .border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💡 HOW TO GET MORE COINS",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF92400E)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• Clear Lines: +15 🪙 per line (boosted by Combos!)\n• Cluster Merges: +8 🪙 per evolved block\n• Clear Levels: Up to +150 🪙 per level\n• Daily Challenge: +250 🪙 + streak bonus!",
                fontSize = 12.sp,
                color = Color(0xFF78350F),
                lineHeight = 18.sp
            )
        }
    }
}
