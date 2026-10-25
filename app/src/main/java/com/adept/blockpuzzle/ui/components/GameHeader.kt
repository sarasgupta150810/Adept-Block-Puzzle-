package com.adept.blockpuzzle.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.adept.blockpuzzle.model.GameMode
import com.adept.blockpuzzle.model.LevelTarget
import com.adept.blockpuzzle.ui.theme.HexColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun GameHeader(
    modifier: Modifier = Modifier,
    score: Int,
    highScore: Int,
    gameMode: GameMode,
    levelNumber: Int,
    levelTarget: LevelTarget?,
    highestTier: Int,
    linesCleared: Int,
    comboStreak: Int = 0,
    comboMultiplier: Int = 1,
    coins: Int = 0,
    onPauseClick: () -> Unit
) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main score pill bar (Matching Panel 2)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF223250), Color(0xFF16233B))
                        )
                    )
                    .border(2.5.dp, Color(0xFF38BDF8), RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("score_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // SCORE
                    Column {
                        Text(
                            text = "SCORE:",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = formatter.format(score),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Divider or Active Combo Streak Badge
                    if (comboStreak >= 2) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        if (comboStreak >= 4) listOf(Color(0xFFFF1744), Color(0xFFFF9100))
                                        else if (comboStreak == 3) listOf(Color(0xFFFF9100), Color(0xFFFFD600))
                                        else listOf(Color(0xFF00E5FF), Color(0xFF00B0FF))
                                    )
                                )
                                .border(1.5.dp, Color.White, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("combo_badge")
                        ) {
                            Text(
                                text = "x$comboMultiplier 🔥",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .width(1.5.dp)
                                .height(32.dp)
                                .background(Color(0x3338BDF8))
                        )
                    }

                    // HIGH SCORE
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (gameMode == GameMode.CLASSIC) "HIGH SCORE:" else "TARGET:",
                            color = Color(0xFFFBBF24),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = formatter.format(if (gameMode == GameMode.CLASSIC) highScore else (levelTarget?.targetScore ?: highScore)),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // COIN BADGE
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF223250), Color(0xFF16233B))
                        )
                    )
                    .border(2.dp, Color(0xFFFBBF24), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp),
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

            Spacer(modifier = Modifier.width(8.dp))

            // PAUSE Button (Matching Panel 2 Pause button)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onPauseClick() }
                    .testTag("pause_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                            )
                        )
                        .border(2.dp, Color.White, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Two vertical pause bars with rounded corners
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(2.5.dp))
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(2.5.dp))
                                .background(Color.White)
                        )
                    }
                }
                Text(
                    text = "PAUSE",
                    color = Color(0xFF1E293B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // In Challenge mode, show Level Goal Banner
        if (gameMode == GameMode.CHALLENGE && levelTarget != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xDD1E293B))
                    .border(1.5.dp, Color(0xFF38BDF8), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
                    .testTag("level_goal_bar")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LVL $levelNumber: ${levelTarget.title}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val goalText = buildString {
                        if (levelTarget.targetEvolutionTier > 0) {
                            append("Tier ${highestTier}/${levelTarget.targetEvolutionTier}  ")
                        }
                        if (levelTarget.targetLinesToClear > 0) {
                            append("Lines ${linesCleared}/${levelTarget.targetLinesToClear}  ")
                        }
                    }
                    Text(
                        text = goalText,
                        color = HexColors.AccentYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
