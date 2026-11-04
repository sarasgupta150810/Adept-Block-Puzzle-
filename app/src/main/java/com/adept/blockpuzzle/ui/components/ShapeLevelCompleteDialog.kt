package com.adept.blockpuzzle.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.adept.blockpuzzle.model.GameTheme
import com.adept.blockpuzzle.model.HexCoord
import com.adept.blockpuzzle.model.ShapeLevelUpReward
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShapeLevelCompleteDialog(
    reward: ShapeLevelUpReward,
    activeTheme: GameTheme = GameTheme.CYBER_NEON,
    onContinue: () -> Unit,
    onClose: () -> Unit
) {
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    // Spring animation for the newly filled block
    val fillScaleAnim = remember { Animatable(0.2f) }
    val fillAlphaAnim = remember { Animatable(0f) }
    val pulseGlowAnim = remember { Animatable(0.5f) }

    LaunchedEffect(reward.newlyFilledIndex) {
        delay(200)
        fillAlphaAnim.animateTo(1f, tween(300))
    }

    LaunchedEffect(reward.newlyFilledIndex) {
        delay(200)
        fillScaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    LaunchedEffect(Unit) {
        pulseGlowAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
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
                .background(Color(0xAA000000))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 350.dp)
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(4.dp, Color(0xFF64748B), RoundedCornerShape(26.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp)
                    .testTag("shape_level_complete_dialog"),
                contentAlignment = Alignment.Center
            ) {
                // Top-right close button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { onClose() }
                        .testTag("shape_dialog_close_button"),
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
                    // Title Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                )
                            )
                            .border(1.5.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LEVEL ${reward.levelNumber} COMPLETE!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }

                    // Score & Subtitle
                    Text(
                        text = "SCORE: ${formatter.format(reward.score)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )

                    // Wonder Artifact Name & Block Fill Progress
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFEEF2F6))
                            .padding(vertical = 6.dp, horizontal = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = reward.shape.iconEmoji, fontSize = 18.sp)
                            Text(
                                text = reward.shape.name.uppercase(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                letterSpacing = 0.8.sp
                            )
                        }
                        val filledNum = (reward.newlyFilledIndex + 1).coerceAtMost(reward.shape.totalBlocks)
                        val (shapePrimary, shapeSecondary) = reward.shape.getThemeColors(activeTheme)
                        Text(
                            text = "Block $filledNum of ${reward.shape.totalBlocks} Filled ✨",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = shapePrimary
                        )
                    }

                    // Shape Display Pedestal Canvas (Styled to match active theme)
                    val (shapePrimary, shapeSecondary) = reward.shape.getThemeColors(activeTheme)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(activeTheme.boardBackdrop, Color(0xFF0B132B))
                                )
                            )
                            .border(2.dp, shapePrimary.copy(alpha = 0.55f), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val shape = reward.shape
                        val newlyFilledIdx = reward.newlyFilledIndex
                        val fillScale = fillScaleAnim.value
                        val fillAlpha = fillAlphaAnim.value
                        val pulseGlow = pulseGlowAnim.value

                        Canvas(modifier = Modifier.fillMaxSize().testTag("shape_mosaic_canvas")) {
                            val canvasCenter = Offset(size.width / 2f, size.height / 2f)
                            val blockRadius = 22.dp.toPx()

                            // Compute center offset of the shape
                            var minX = Float.MAX_VALUE
                            var maxX = -Float.MAX_VALUE
                            var minY = Float.MAX_VALUE
                            var maxY = -Float.MAX_VALUE

                            val offsets = shape.blocks.map { block ->
                                val pos = block.coord.toScreenOffset(blockRadius, Offset.Zero)
                                minX = minOf(minX, pos.x)
                                maxX = maxOf(maxX, pos.x)
                                minY = minOf(minY, pos.y)
                                maxY = maxOf(maxY, pos.y)
                                pos
                            }

                            val shapeCenter = Offset((minX + maxX) / 2f, (minY + maxY) / 2f)
                            val drawCenter = canvasCenter - shapeCenter

                            // Draw each block in the shape
                            for ((i, block) in shape.blocks.withIndex()) {
                                val blockPos = drawCenter + offsets[i]

                                when {
                                    // 1. Blocks filled before this level
                                    i < newlyFilledIdx -> {
                                        drawSolidHexBlock(
                                            center = blockPos,
                                            radius = blockRadius * 0.92f,
                                            primaryColor = shapePrimary,
                                            secondaryColor = shapeSecondary
                                        )
                                    }

                                    // 2. THE NEWLY FILLED BLOCK (Animated!)
                                    i == newlyFilledIdx -> {
                                        // Radiant outer pulse glow
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                listOf(
                                                    shapeSecondary.copy(alpha = 0.65f * pulseGlow),
                                                    Color.Transparent
                                                ),
                                                center = blockPos,
                                                radius = blockRadius * 1.8f
                                            ),
                                            radius = blockRadius * 1.8f,
                                            center = blockPos
                                        )

                                        // Draw base placeholder slot matching board empty cell
                                        drawEmptySlotHexBlock(
                                            center = blockPos,
                                            radius = blockRadius * 0.92f,
                                            accentColor = if (shape.id == "hex_shield") activeTheme.emptyCellBorder else shapePrimary.copy(alpha = 0.4f),
                                            fillColor = if (shape.id == "hex_shield") activeTheme.emptyCellFill else Color(0x22000000)
                                        )

                                        // Animated filled block popping into existence
                                        if (fillAlpha > 0f) {
                                            drawSolidHexBlock(
                                                center = blockPos,
                                                radius = blockRadius * 0.92f * fillScale,
                                                primaryColor = shapePrimary,
                                                secondaryColor = shapeSecondary,
                                                alpha = fillAlpha
                                            )
                                        }
                                    }

                                    // 3. Blocks yet to be filled (match board's empty cells for hex shield)
                                    else -> {
                                        drawEmptySlotHexBlock(
                                            center = blockPos,
                                            radius = blockRadius * 0.92f,
                                            accentColor = if (shape.id == "hex_shield") activeTheme.emptyCellBorder else Color(0x33FFFFFF),
                                            fillColor = if (shape.id == "hex_shield") activeTheme.emptyCellFill else Color(0x22000000)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Special Banner if Shape Fully Restored!
                    if (reward.isShapeFullyCompleted) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFF8B5CF6))
                                    )
                                )
                                .padding(vertical = 8.dp, horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🌟 WONDER FULLY RESTORED! 🌟",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "BONUS: +250 🪙 COINS & +1 HAMMER 🔨",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFEF08A)
                                )
                            }
                        }
                    } else {
                        // Standard Level Coins Reward
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
                                text = "+${reward.coinsEarned} 🪙 COINS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD97706)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Primary Action Button (Continue infinite run or next level)
                    JuicyButton(
                        text = if (reward.isInfiniteMode) "CONTINUE RUN 🚀" else "NEXT LEVEL ➡️",
                        topColor = Color(0xFF8AE81B),
                        bottomColor = Color(0xFF4CA606),
                        borderColor = Color(0xFF2B6B00),
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth().testTag("continue_run_button")
                    )

                    // Secondary: Quit to Menu
                    JuicyButton(
                        text = "MAIN MENU",
                        topColor = Color(0xFF38BDF8),
                        bottomColor = Color(0xFF0284C7),
                        borderColor = Color(0xFF0369A1),
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth().testTag("dialog_main_menu_button")
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawSolidHexBlock(
    center: Offset,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    alpha: Float = 1.0f
) {
    val path = Path()
    for (i in 0 until 6) {
        val angleRad = (60.0 * i - 30.0) * PI / 180.0
        val x = (center.x + radius * cos(angleRad)).toFloat()
        val y = (center.y + radius * sin(angleRad)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    // 1. Main gradient fill
    drawPath(
        path = path,
        brush = Brush.radialGradient(
            colors = listOf(
                secondaryColor.copy(alpha = alpha),
                primaryColor.copy(alpha = alpha)
            ),
            center = Offset(center.x - radius * 0.2f, center.y - radius * 0.3f),
            radius = radius * 1.2f
        ),
        style = Fill
    )

    // 2. Crisp bright edge highlight
    drawPath(
        path = path,
        color = Color.White.copy(alpha = 0.55f * alpha),
        style = Stroke(width = 2.dp.toPx())
    )

    // 3. Specular upper-left gloss bubble
    drawCircle(
        color = Color.White.copy(alpha = 0.40f * alpha),
        radius = radius * 0.32f,
        center = Offset(center.x - radius * 0.22f, center.y - radius * 0.28f)
    )
}

private fun DrawScope.drawEmptySlotHexBlock(
    center: Offset,
    radius: Float,
    accentColor: Color,
    fillColor: Color = Color(0x22000000)
) {
    val path = Path()
    for (i in 0 until 6) {
        val angleRad = (60.0 * i - 30.0) * PI / 180.0
        val x = (center.x + radius * cos(angleRad)).toFloat()
        val y = (center.y + radius * sin(angleRad)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    // Base background cell fill
    drawPath(
        path = path,
        color = fillColor,
        style = Fill
    )

    // Wireframe outline
    drawPath(
        path = path,
        color = accentColor,
        style = Stroke(width = 1.8.dp.toPx())
    )

    // Small dashed center cross / plus indicator
    val crossLen = radius * 0.25f
    drawLine(
        color = accentColor.copy(alpha = 0.6f),
        start = Offset(center.x - crossLen, center.y),
        end = Offset(center.x + crossLen, center.y),
        strokeWidth = 1.5.dp.toPx()
    )
    drawLine(
        color = accentColor.copy(alpha = 0.6f),
        start = Offset(center.x, center.y - crossLen),
        end = Offset(center.x, center.y + crossLen),
        strokeWidth = 1.5.dp.toPx()
    )
}
