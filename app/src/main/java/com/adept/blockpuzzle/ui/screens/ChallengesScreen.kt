package com.adept.blockpuzzle.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adept.blockpuzzle.game.DailyChallengeManager
import com.adept.blockpuzzle.game.LevelCatalog
import com.adept.blockpuzzle.model.LevelTarget
import com.adept.blockpuzzle.ui.components.JuicyButton
import com.adept.blockpuzzle.ui.components.SkyHexBokehBackground
import com.adept.blockpuzzle.ui.theme.HexColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ChallengesScreen(
    unlockedLevels: Set<Int>,
    levelStarsMap: Map<Int, Int>,
    levelScoresMap: Map<Int, Int>,
    isDailyCompleted: Boolean,
    dailyStreak: Int,
    coins: Int = 0,
    activeTheme: com.adept.blockpuzzle.model.GameTheme = com.adept.blockpuzzle.model.GameTheme.CYBER_NEON,
    onSelectLevel: (Int) -> Unit,
    onPlayDaily: () -> Unit,
    onOpenStore: () -> Unit = {},
    onBack: () -> Unit
) {
    // Infinite Levels: dynamically render up to maxUnlocked + 16 levels (minimum 24)
    val maxUnlocked = unlockedLevels.maxOrNull() ?: 1
    val totalLevelsToShow = maxOf(24, maxUnlocked + 16)
    val levels = remember(totalLevelsToShow) {
        (1..totalLevelsToShow).map { LevelCatalog.getLevel(it) }
    }

    val todayChallenge = remember { DailyChallengeManager.getDailyChallenge() }
    val formatter = NumberFormat.getNumberInstance(Locale.US)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("challenges_screen")
    ) {
        // Sky Blue Bokeh Wallpaper with active theme
        SkyHexBokehBackground(theme = activeTheme)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(2.dp, Color(0xFF38BDF8), CircleShape)
                        .clickable { onBack() }
                        .testTag("challenges_back_button"),
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
                    text = "CHALLENGES",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B),
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Coin Balance Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF223250), Color(0xFF16233B))
                            )
                        )
                        .border(2.dp, Color(0xFFFBBF24), RoundedCornerShape(18.dp))
                        .clickable { onOpenStore() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("challenges_coin_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Text(
                            text = formatter.format(coins),
                            color = Color(0xFFFDE047),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Daily Challenge Hero Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E2C48), Color(0xFF131D32))
                        )
                    )
                    .border(2.5.dp, Color(0xFFFBBF24), RoundedCornerShape(20.dp))
                    .padding(14.dp)
                    .testTag("daily_challenge_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Daily",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DAILY CHALLENGE",
                                color = Color(0xFFFBBF24),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        // Daily Streak badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x33FFB300))
                                .border(1.dp, Color(0x66FFB300), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🔥 $dailyStreak DAY STREAK",
                                color = HexColors.AccentYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = todayChallenge.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = todayChallenge.description,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reward: 👑 Daily Crown",
                            color = Color(0xFF86EFAC),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        JuicyButton(
                            text = if (isDailyCompleted) "REPLAY 👑" else "PLAY TRIAL ➔",
                            topColor = if (isDailyCompleted) Color(0xFFFBBF24) else Color(0xFF8AE81B),
                            bottomColor = if (isDailyCompleted) Color(0xFFD97706) else Color(0xFF4CA606),
                            borderColor = if (isDailyCompleted) Color(0xFF92400E) else Color(0xFF2B6B00),
                            onClick = onPlayDaily,
                            modifier = Modifier
                                .width(135.dp)
                                .height(44.dp)
                                .testTag("play_daily_button")
                        )
                    }
                }
            }

            // Infinite Stages Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INFINITE STAGES",
                    color = Color(0xFF1E293B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "MAX LEVEL: $maxUnlocked",
                    color = Color(0xFF0369A1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Grid of Infinite Levels
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 145.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().testTag("levels_grid")
            ) {
                items(levels) { level ->
                    val isUnlocked = unlockedLevels.contains(level.levelNumber) || level.levelNumber == 1
                    val stars = levelStarsMap[level.levelNumber] ?: 0
                    val bestScore = levelScoresMap[level.levelNumber] ?: 0

                    LevelCard(
                        level = level,
                        isUnlocked = isUnlocked,
                        stars = stars,
                        bestScore = bestScore,
                        onClick = {
                            if (isUnlocked) {
                                onSelectLevel(level.levelNumber)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LevelCard(
    level: LevelTarget,
    isUnlocked: Boolean,
    stars: Int,
    bestScore: Int,
    onClick: () -> Unit
) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    val isBossStage = level.levelNumber % 5 == 0

    val backgroundBrush = if (isUnlocked) {
        if (isBossStage) {
            Brush.verticalGradient(listOf(Color(0xFF3B183E), Color(0xFF210E24)))
        } else {
            Brush.verticalGradient(listOf(Color(0xFF223250), Color(0xFF16233B)))
        }
    } else {
        Brush.verticalGradient(listOf(Color(0xFF181C26), Color(0xFF0E1118)))
    }

    val borderColor = if (isUnlocked) {
        if (isBossStage) Color(0xFFE879F9)
        else if (stars >= 3) HexColors.AccentYellow
        else Color(0xFF38BDF8)
    } else {
        Color(0xFF334155)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(142.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundBrush)
            .border(2.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable(enabled = isUnlocked) { onClick() }
            .padding(10.dp)
            .testTag("level_card_${level.levelNumber}")
    ) {
        if (!isUnlocked) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "LEVEL ${level.levelNumber}",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LVL ${level.levelNumber}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (isBossStage) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "BOSS ⚔️",
                                color = Color(0xFFE879F9),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        for (s in 1..3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star $s",
                                tint = if (stars >= s) HexColors.AccentYellow else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Text(
                    text = level.title,
                    color = if (isBossStage) Color(0xFFF472B6) else Color(0xFF93C5FD),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = level.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Target: ${formatter.format(level.targetScore)}",
                        color = Color(0xFF86EFAC),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (bestScore > 0) {
                        Text(
                            text = "Best: ${formatter.format(bestScore)}",
                            color = HexColors.AccentYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
