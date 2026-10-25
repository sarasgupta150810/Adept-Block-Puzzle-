package com.adept.blockpuzzle.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adept.blockpuzzle.R
import com.adept.blockpuzzle.ui.components.JuicyButton
import com.adept.blockpuzzle.ui.components.SkyHexBokehBackground

@Composable
fun MainMenuScreen(
    soundEnabled: Boolean,
    dailyStreak: Int = 0,
    isDailyCompleted: Boolean = false,
    coins: Int = 0,
    activeTheme: com.adept.blockpuzzle.model.GameTheme = com.adept.blockpuzzle.model.GameTheme.CYBER_NEON,
    onPlayGame: () -> Unit,
    onPlayDaily: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenStore: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleSound: () -> Unit
) {
    val floatAnim = remember { Animatable(0f) }
    val formatter = remember { java.text.NumberFormat.getNumberInstance(java.util.Locale.US) }
    LaunchedEffect(Unit) {
        floatAnim.animateTo(
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_menu_screen"),
        contentAlignment = Alignment.Center
    ) {
        // High-definition Radiant Wallpaper with active theme
        SkyHexBokehBackground(theme = activeTheme)

        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: package label & clickable Coin Balance Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "'com.saras.blockpuzzle'",
                    color = Color(0xFF1E293B).copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
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
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("menu_coin_pill"),
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
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // 3D Embossed "BLOCK EVOLUTION" Logo (Matching Panel 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.padding(bottom = floatAnim.value.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_block_evolution),
                        contentDescription = "Block Evolution",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PLAY GAME BUTTON (Big Vibrant Green Pill Button, Matching Panel 1)
            JuicyButton(
                text = "PLAY GAME",
                topColor = Color(0xFF8AE81B),
                bottomColor = Color(0xFF4CA606),
                borderColor = Color(0xFF2B6B00),
                onClick = onPlayGame,
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .height(64.dp)
                    .testTag("play_game_button")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // DAILY CHALLENGE BUTTON (Golden Amber Pill Button with Flame Streak)
            val dailyText = if (isDailyCompleted) "DAILY COMPLETED ✓" else if (dailyStreak > 0) "DAILY CHALLENGE 🔥$dailyStreak" else "DAILY CHALLENGE"
            JuicyButton(
                text = dailyText,
                topColor = Color(0xFFFBBF24),
                bottomColor = Color(0xFFD97706),
                borderColor = Color(0xFFB45309),
                onClick = onPlayDaily,
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .height(58.dp)
                    .testTag("daily_challenge_button")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // CHALLENGES BUTTON (Big Vibrant Sky Blue Pill Button, Matching Panel 1)
            JuicyButton(
                text = "CHALLENGES",
                topColor = Color(0xFF38BDF8),
                bottomColor = Color(0xFF0284C7),
                borderColor = Color(0xFF0369A1),
                onClick = onOpenChallenges,
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .height(58.dp)
                    .testTag("challenges_button")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // BOTTOM ROW: SETTINGS, STORE & SOUND BUTTONS
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                // Settings button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onOpenSettings() }
                        .testTag("menu_settings_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                )
                            )
                            .border(2.5.dp, Color.White, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = "SETTINGS",
                        color = Color(0xFF1E293B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Store button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onOpenStore() }
                        .testTag("menu_store_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFBBF24), Color(0xFFD97706))
                                )
                            )
                            .border(2.5.dp, Color.White, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛍️", fontSize = 26.sp)
                    }
                    Text(
                        text = "STORE",
                        color = Color(0xFF1E293B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Sound / Speaker toggle button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onToggleSound() }
                        .testTag("menu_sound_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                )
                            )
                            .border(2.5.dp, Color.White, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Sound Toggle",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = if (soundEnabled) "SOUND ON" else "SOUND OFF",
                        color = Color(0xFF1E293B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
