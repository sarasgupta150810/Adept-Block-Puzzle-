package com.adept.blockpuzzle.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BoosterActionBar(
    modifier: Modifier = Modifier,
    hammersCount: Int,
    shufflesCount: Int,
    rainbowCount: Int,
    coins: Int,
    isHammerMode: Boolean,
    onToggleHammer: () -> Unit,
    onShuffle: () -> Unit,
    onRainbowHex: () -> Unit,
    onOpenStore: () -> Unit
) {
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }
    val hammerPulse = remember { Animatable(1f) }

    LaunchedEffect(isHammerMode) {
        if (isHammerMode) {
            hammerPulse.animateTo(
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            hammerPulse.snapTo(1f)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ACTIVE HAMMER PROMPT BANNER
        AnimatedVisibility(
            visible = isHammerMode,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFDC2626), Color(0xFFEA580C))
                        )
                    )
                    .border(2.dp, Color(0xFFFDE047), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔨 TAP ANY TILE TO DESTROY IT!",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x55000000))
                            .clickable { onToggleHammer() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CANCEL ✖",
                            color = Color(0xFFFDE047),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // ACTION BUTTONS ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BOOSTERS GROUP
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. HAMMER BUTTON
                BoosterButton(
                    emoji = "🔨",
                    label = "SMASH",
                    count = hammersCount,
                    isActive = isHammerMode,
                    pulseScale = if (isHammerMode) hammerPulse.value else 1f,
                    testTag = "booster_hammer_button",
                    onClick = onToggleHammer
                )

                // 2. SHUFFLE BUTTON
                BoosterButton(
                    emoji = "🔄",
                    label = "SHUFFLE",
                    count = shufflesCount,
                    isActive = false,
                    testTag = "booster_shuffle_button",
                    onClick = onShuffle
                )

                // 3. RAINBOW 1x1 BUTTON
                BoosterButton(
                    emoji = "🌈",
                    label = "WILD 1x1",
                    count = rainbowCount,
                    isActive = false,
                    testTag = "booster_rainbow_button",
                    onClick = onRainbowHex
                )
            }

            // QUICK STORE ACCESS PILL
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
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("in_game_store_pill"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🪙", fontSize = 15.sp)
                    Text(
                        text = formatter.format(coins),
                        color = Color(0xFFFDE047),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoosterButton(
    emoji: String,
    label: String,
    count: Int,
    isActive: Boolean,
    pulseScale: Float = 1f,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .scale(pulseScale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    if (isActive) listOf(Color(0xFFEF4444), Color(0xFFB91C1C))
                    else listOf(Color(0xFF25354E), Color(0xFF1A263B))
                )
            )
            .border(
                width = if (isActive) 2.5.dp else 1.5.dp,
                color = if (isActive) Color(0xFFFDE047) else Color(0xFF38BDF8),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isActive) Color(0xFFFDE047) else Color(0xFF93C5FD),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "x$count",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}
