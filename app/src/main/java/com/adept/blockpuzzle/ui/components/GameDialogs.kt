package com.adept.blockpuzzle.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.adept.blockpuzzle.R
import com.adept.blockpuzzle.ui.theme.HexColors
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onSettings: () -> Unit,
    onQuit: () -> Unit
) {
    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable { onResume() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 330.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(4.dp, Color(0xFF64748B), RoundedCornerShape(26.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp)
                    .testTag("pause_dialog"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Banner (Matching Panel 3)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                                )
                            )
                            .border(1.5.dp, Color(0xFF94A3B8), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GAME PAUSED",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B),
                            letterSpacing = 1.2.sp
                        )
                    }

                    // RESUME (Green Button)
                    JuicyButton(
                        text = "RESUME",
                        topColor = Color(0xFF8AE81B),
                        bottomColor = Color(0xFF4CA606),
                        borderColor = Color(0xFF2B6B00),
                        onClick = onResume,
                        modifier = Modifier.fillMaxWidth().testTag("resume_button")
                    )

                    // RESTART LEVEL (Blue Button)
                    JuicyButton(
                        text = "RESTART LEVEL",
                        topColor = Color(0xFF38BDF8),
                        bottomColor = Color(0xFF0284C7),
                        borderColor = Color(0xFF0369A1),
                        onClick = onRestart,
                        modifier = Modifier.fillMaxWidth().testTag("restart_button")
                    )

                    // Row: SETTINGS & QUIT
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        JuicyButton(
                            text = "SETTINGS",
                            topColor = Color(0xFF38BDF8),
                            bottomColor = Color(0xFF0284C7),
                            borderColor = Color(0xFF0369A1),
                            onClick = onSettings,
                            modifier = Modifier.weight(1f).testTag("pause_settings_button")
                        )

                        JuicyButton(
                            text = "QUIT",
                            topColor = Color(0xFFF87171),
                            bottomColor = Color(0xFFDC2626),
                            borderColor = Color(0xFF991B1B),
                            onClick = onQuit,
                            modifier = Modifier.weight(1f).testTag("quit_button")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    musicEnabled: Boolean,
    soundFxEnabled: Boolean,
    brightness: Float,
    onToggleMusic: () -> Unit,
    onToggleSoundFx: () -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 330.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(4.dp, Color(0xFF64748B), RoundedCornerShape(26.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp)
                    .testTag("settings_dialog")
            ) {
                // Red Close 'X' Button on top-right (Matching Panel 4)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { onClose() }
                        .testTag("settings_close_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Settings",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    // Header (Matching Panel 4)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                                )
                            )
                            .border(1.5.dp, Color(0xFF94A3B8), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SETTINGS",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B),
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // MUSIC TOGGLE
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MUSIC",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Switch(
                            checked = musicEnabled,
                            onCheckedChange = { onToggleMusic() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF22C55E),
                                uncheckedThumbColor = Color(0xFFCBD5E1),
                                uncheckedTrackColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("music_switch")
                        )
                    }

                    // SOUND FX TOGGLE
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SOUND FX",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Switch(
                            checked = soundFxEnabled,
                            onCheckedChange = { onToggleSoundFx() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF22C55E),
                                uncheckedThumbColor = Color(0xFFCBD5E1),
                                uncheckedTrackColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("sound_fx_switch")
                        )
                    }

                    // BRIGHTNESS SLIDER
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "BRIGHTNESS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Slider(
                            value = brightness,
                            onValueChange = onBrightnessChange,
                            valueRange = 0.3f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF38BDF8),
                                activeTrackColor = Color(0xFF0284C7),
                                inactiveTrackColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.testTag("brightness_slider")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelCompleteDialog(
    levelNumber: Int,
    score: Int,
    stars: Int,
    onNextLevel: () -> Unit,
    onClose: () -> Unit
) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    val starScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        starScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 330.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(4.dp, Color(0xFF64748B), RoundedCornerShape(26.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp)
                    .testTag("level_complete_dialog"),
                contentAlignment = Alignment.Center
            ) {
                // Red Close 'X' Button on top-right (Matching Panel 5)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { onClose() }
                        .testTag("level_complete_close_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    // 3D Golden Victory Stars Graphic (Matching Panel 5)
                    Image(
                        painter = painterResource(id = R.drawable.ic_golden_stars),
                        contentDescription = "Golden Stars",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(95.dp)
                            .scale(starScale.value)
                    )

                    // Banner Title (Matching Panel 5)
                    Text(
                        text = "LEVEL COMPLETE",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD97706),
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "LEVEL $levelNumber CLEAR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF16A34A)
                    )

                    Text(
                        text = "SCORE: ${formatter.format(score)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )

                    // COIN REWARD ROW
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "REWARD:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309)
                        )
                        Text(
                            text = "+${stars.coerceAtLeast(1) * 50} 🪙",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFD97706)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // NEXT LEVEL BUTTON (Matching Panel 5)
                    JuicyButton(
                        text = "NEXT LEVEL",
                        topColor = Color(0xFF8AE81B),
                        bottomColor = Color(0xFF4CA606),
                        borderColor = Color(0xFF2B6B00),
                        onClick = onNextLevel,
                        modifier = Modifier.fillMaxWidth().testTag("next_level_button")
                    )
                }
            }
        }
    }
}

@Composable
fun GameOverDialog(
    score: Int,
    onRetry: () -> Unit,
    onQuit: () -> Unit,
    onOpenStore: (() -> Unit)? = null
) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    val skullScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        skullScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Dialog(
        onDismissRequest = onQuit,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable { onQuit() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 330.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(4.dp, Color(0xFF64748B), RoundedCornerShape(26.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp)
                    .testTag("game_over_dialog"),
                contentAlignment = Alignment.Center
            ) {
                // Red Close 'X' Button on top-right (Matching Panel 6)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { onQuit() }
                        .testTag("game_over_close_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    // Cartoon Skull Graphic (Matching Panel 6)
                    Image(
                        painter = painterResource(id = R.drawable.ic_game_over_skull),
                        contentDescription = "Game Over Skull",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(85.dp)
                            .scale(skullScale.value)
                    )

                    Text(
                        text = "LEVEL FAILED",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFDC2626),
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "SCORE: ${formatter.format(score)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // RETRY Button (Green, Matching Panel 6)
                    JuicyButton(
                        text = "RETRY",
                        topColor = Color(0xFF8AE81B),
                        bottomColor = Color(0xFF4CA606),
                        borderColor = Color(0xFF2B6B00),
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth().testTag("retry_button")
                    )

                    // SHOP BOOSTERS (Amber button)
                    if (onOpenStore != null) {
                        JuicyButton(
                            text = "SHOP BOOSTERS 🛍️",
                            topColor = Color(0xFFFBBF24),
                            bottomColor = Color(0xFFD97706),
                            borderColor = Color(0xFFB45309),
                            onClick = onOpenStore,
                            modifier = Modifier.fillMaxWidth().testTag("game_over_shop_button")
                        )
                    }

                    // QUIT TO MENU Button (Blue, Matching Panel 6)
                    JuicyButton(
                        text = "QUIT TO MENU",
                        topColor = Color(0xFF38BDF8),
                        bottomColor = Color(0xFF0284C7),
                        borderColor = Color(0xFF0369A1),
                        onClick = onQuit,
                        modifier = Modifier.fillMaxWidth().testTag("quit_to_menu_button")
                    )
                }
            }
        }
    }
}

/**
 * Super-juicy, tactile 3D arcade button with press physics and glossy specular highlight.
 */
@Composable
fun JuicyButton(
    text: String,
    topColor: Color,
    bottomColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val yOffset = if (isPressed && enabled) 4.dp else 0.dp
    val bevelHeight = if (isPressed && enabled) 2.dp else 6.dp

    Box(
        modifier = modifier
            .offset(y = yOffset)
            .clickable(interactionSource = interactionSource, indication = null, enabled = enabled) { onClick() }
    ) {
        // 3D Bottom Bevel / Extrusion Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .offset(y = bevelHeight)
                .clip(RoundedCornerShape(22.dp))
                .background(borderColor)
        )

        // Main Button Face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.verticalGradient(listOf(topColor, bottomColor)))
                .border(2.5.dp, borderColor, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Upper half white gloss reflection pill
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.92f)
                    .height(24.dp)
                    .padding(top = 3.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.50f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        )
                    )
            )

            // Button Text
            Text(
                text = text,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            )
        }
    }
}
