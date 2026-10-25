package com.adept.blockpuzzle.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import com.adept.blockpuzzle.model.CellData
import com.adept.blockpuzzle.model.FloatingText
import com.adept.blockpuzzle.model.HexCoord
import com.adept.blockpuzzle.model.HexPiece
import com.adept.blockpuzzle.model.Particle
import com.adept.blockpuzzle.ui.theme.HexColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun HexBoardCanvas(
    modifier: Modifier = Modifier,
    board: Map<HexCoord, CellData>,
    hoverPiece: HexPiece?,
    hoverAnchor: HexCoord?,
    isHoverValid: Boolean,
    particles: List<Particle>,
    floatingTexts: List<FloatingText>,
    brightness: Float,
    activeTheme: com.adept.blockpuzzle.model.GameTheme = com.adept.blockpuzzle.model.GameTheme.CYBER_NEON,
    isHammerMode: Boolean = false,
    onBoardMetricsChanged: (centerInRoot: Offset, localCenter: Offset, hexRadius: Float) -> Unit
) {
    val pulseAnim = remember { Animatable(0.92f) }
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(550, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("hex_board_container")
            .onGloballyPositioned { coordinates ->
                val rootPos = coordinates.positionInRoot()
                val w = coordinates.size.width.toFloat()
                val h = coordinates.size.height.toFloat()
                val centerInRoot = Offset(rootPos.x + w / 2f, rootPos.y + h / 2f)
                val localCenter = Offset(w / 2f, h / 2f)

                val maxRByWidth = (w * 0.94f) / 14f
                val maxRByHeight = (h * 0.94f) / (9f * sqrt(3.0f))
                val hexRadius = minOf(maxRByWidth, maxRByHeight).coerceIn(16f, 48f)

                onBoardMetricsChanged(centerInRoot, localCenter, hexRadius)
            }
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val localCenter = Offset(width / 2f, height / 2f)

        val maxRByWidth = (width * 0.94f) / 14f
        val maxRByHeight = (height * 0.94f) / (9f * sqrt(3.0f))
        val hexRadius = minOf(maxRByWidth, maxRByHeight).coerceIn(16f, 48f)

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("hex_board_canvas")
        ) {
            // 1. Outer Dark Chassis Hexagon (Matching Panel 2)
            val outerRadius = hexRadius * 4.95f
            drawRoundedFlatHexagon(
                center = Offset(localCenter.x, localCenter.y + 5f),
                radius = outerRadius,
                cornerRadius = outerRadius * 0.08f,
                fillColor = Color(0x55000000)
            )
            drawRoundedFlatHexagon(
                center = localCenter,
                radius = outerRadius,
                cornerRadius = outerRadius * 0.08f,
                fillColor = activeTheme.boardBackdrop,
                strokeColor = activeTheme.boardBorder,
                strokeWidth = 7f
            )
            drawRoundedFlatHexagon(
                center = localCenter,
                radius = outerRadius - 3.5f,
                cornerRadius = outerRadius * 0.08f,
                fillColor = Color.Transparent,
                strokeColor = Color(0x55000000),
                strokeWidth = 3f
            )

            // 2. Empty Grid Sockets (Rounded dark recessed cups)
            for (coord in HexCoord.ALL_GRID_CELLS) {
                val cellPos = coord.toScreenOffset(hexRadius, localCenter)
                // Inset socket shadow
                drawRoundedFlatHexagon(
                    center = Offset(cellPos.x, cellPos.y + 1.8f),
                    radius = hexRadius - 2.5f,
                    cornerRadius = hexRadius * 0.16f,
                    fillColor = Color(0xFF101726)
                )
                // Empty cell body
                drawRoundedFlatHexagon(
                    center = cellPos,
                    radius = hexRadius - 2.5f,
                    cornerRadius = hexRadius * 0.16f,
                    fillColor = activeTheme.emptyCellFill,
                    strokeColor = activeTheme.emptyCellBorder,
                    strokeWidth = 2.5f
                )
            }

            // 3. Placed 3D Tactile Rounded Candy Blocks
            for ((coord, cell) in board) {
                if (cell.value == 0) continue
                val cellPos = coord.toScreenOffset(hexRadius, localCenter)

                if (cell.isLocked || cell.value == -1) {
                    drawRoundedFlatHexagon(
                        center = cellPos,
                        radius = hexRadius - 2.5f,
                        cornerRadius = hexRadius * 0.16f,
                        fillColor = HexColors.LockedCellFill,
                        strokeColor = HexColors.LockedCellBorder,
                        strokeWidth = 3f
                    )
                    drawTextOnHex("🔒", cellPos, hexRadius * 0.7f, Color.White.toArgb())
                } else {
                    val (c1, c2) = HexColors.getColorsForTier(cell.value)
                    drawProfessional3DHex(
                        center = cellPos,
                        radius = hexRadius - 2.5f,
                        topColor = c1,
                        bottomColor = c2,
                        numberText = cell.value.toString(),
                        isRecentlyMerged = cell.isRecentlyMerged,
                        pulseScale = if (cell.isRecentlyMerged) pulseAnim.value else 1f
                    )

                    // If in Hammer mode, draw targeting crosshair / smash ring
                    if (isHammerMode) {
                        drawRoundedFlatHexagon(
                            center = cellPos,
                            radius = (hexRadius - 1f) * pulseAnim.value,
                            cornerRadius = hexRadius * 0.16f,
                            fillColor = Color.Transparent,
                            strokeColor = Color(0xFFFDE047),
                            strokeWidth = 3.5f
                        )
                    }
                }
            }

            // 4. Hover Placement Preview
            if (hoverPiece != null && hoverAnchor != null) {
                for (i in hoverPiece.coords.indices) {
                    val target = hoverAnchor + hoverPiece.coords[i]
                    if (target.distanceToCenter() <= HexCoord.GRID_RADIUS) {
                        val pos = target.toScreenOffset(hexRadius, localCenter)
                        val previewValue = hoverPiece.values.getOrElse(i) { hoverPiece.baseValue }
                        val (c1, c2) = HexColors.getColorsForTier(previewValue)

                        if (isHoverValid) {
                            drawProfessional3DHex(
                                center = pos,
                                radius = (hexRadius - 2.5f) * pulseAnim.value,
                                topColor = c1.copy(alpha = 0.82f),
                                bottomColor = c2.copy(alpha = 0.82f),
                                numberText = previewValue.toString(),
                                isRecentlyMerged = true
                            )
                        } else {
                            drawRoundedFlatHexagon(
                                center = pos,
                                radius = hexRadius - 2.5f,
                                cornerRadius = hexRadius * 0.16f,
                                fillColor = Color.Red.copy(alpha = 0.45f),
                                strokeColor = Color.Red.copy(alpha = 0.90f),
                                strokeWidth = 3f
                            )
                        }
                    }
                }
            }

            // 5. Magic Sparks, Particle Stars & Rising Orbs (Matching center of Panel 2)
            for (p in particles) {
                val alpha = p.life.coerceIn(0f, 1f)
                val pColor = p.color.copy(alpha = alpha)
                if (p.isStar) {
                    drawStar(Offset(p.x, p.y), p.size * alpha, pColor)
                } else {
                    drawCircle(color = pColor.copy(alpha = alpha * 0.35f), radius = p.size * 1.8f * alpha, center = Offset(p.x, p.y))
                    drawCircle(color = pColor, radius = p.size * alpha, center = Offset(p.x, p.y))
                }
            }

            // 6. Floating Text Popups (+500, EVOLVED!, COMBO x2!)
            for (ft in floatingTexts) {
                val alpha = (1f - ft.progress).coerceIn(0f, 1f)
                val paint = Paint().apply {
                    color = ft.color.copy(alpha = alpha).toArgb()
                    textSize = ft.sizeSp * (1f + ft.progress * 0.25f)
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    setShadowLayer(10f, 0f, 4f, Color.Black.copy(alpha = alpha * 0.9f).toArgb())
                }
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(ft.text, ft.position.x, ft.position.y, paint)
                }
            }

            if (brightness < 1.0f) {
                drawRect(
                    color = Color.Black.copy(alpha = (1f - brightness) * 0.7f),
                    size = Size(width, height)
                )
            }
        }
    }
}

/**
 * Creates FLAT-TOPPED regular hexagon path with smooth rounded corners.
 */
fun createRoundedFlatHexPath(
    center: Offset,
    radius: Float,
    cornerRadius: Float = radius * 0.16f
): Path {
    val path = Path()
    val vertices = Array(6) { i ->
        val angleRad = Math.toRadians(60.0 * i)
        Offset(
            center.x + radius * cos(angleRad).toFloat(),
            center.y + radius * sin(angleRad).toFloat()
        )
    }

    for (i in 0 until 6) {
        val curr = vertices[i]
        val prev = vertices[(i + 5) % 6]
        val next = vertices[(i + 1) % 6]

        val toPrev = prev - curr
        val distPrev = sqrt(toPrev.x * toPrev.x + toPrev.y * toPrev.y).coerceAtLeast(1f)
        val pNorm = Offset(toPrev.x / distPrev, toPrev.y / distPrev)

        val toNext = next - curr
        val distNext = sqrt(toNext.x * toNext.x + toNext.y * toNext.y).coerceAtLeast(1f)
        val nNorm = Offset(toNext.x / distNext, toNext.y / distNext)

        val startCorner = curr + pNorm * cornerRadius
        val endCorner = curr + nNorm * cornerRadius

        if (i == 0) {
            path.moveTo(startCorner.x, startCorner.y)
        } else {
            path.lineTo(startCorner.x, startCorner.y)
        }
        path.quadraticBezierTo(curr.x, curr.y, endCorner.x, endCorner.y)
    }
    path.close()
    return path
}

fun DrawScope.drawRoundedFlatHexagon(
    center: Offset,
    radius: Float,
    cornerRadius: Float = radius * 0.16f,
    fillColor: Color,
    strokeColor: Color = Color.Transparent,
    strokeWidth: Float = 0f
) {
    val path = createRoundedFlatHexPath(center, radius, cornerRadius)
    drawPath(path = path, color = fillColor, style = Fill)
    if (strokeWidth > 0f && strokeColor != Color.Transparent) {
        drawPath(path = path, color = strokeColor, style = Stroke(width = strokeWidth))
    }
}

/**
 * Renders a tactile, juicy 3D plastic/candy hexagon tile matching the reference screenshot.
 */
fun DrawScope.drawProfessional3DHex(
    center: Offset,
    radius: Float,
    topColor: Color,
    bottomColor: Color,
    numberText: String,
    isRecentlyMerged: Boolean = false,
    pulseScale: Float = 1f
) {
    val r = radius * pulseScale
    val cornerR = r * 0.16f

    // 1. Soft Drop Shadow
    drawRoundedFlatHexagon(
        center = Offset(center.x, center.y + 4.5f),
        radius = r - 0.5f,
        cornerRadius = cornerR,
        fillColor = Color(0x4D000000)
    )

    // 2. 3D Bottom Extrusion / Bevel Facet
    val bevelColor = Color(
        red = (bottomColor.red * 0.65f).coerceIn(0f, 1f),
        green = (bottomColor.green * 0.65f).coerceIn(0f, 1f),
        blue = (bottomColor.blue * 0.65f).coerceIn(0f, 1f),
        alpha = bottomColor.alpha
    )
    drawRoundedFlatHexagon(
        center = Offset(center.x, center.y + 3.0f),
        radius = r,
        cornerRadius = cornerR,
        fillColor = bevelColor
    )

    // 3. Main Face with Vertical Gradient
    val facePath = createRoundedFlatHexPath(center, r, cornerR)
    val faceBrush = Brush.verticalGradient(
        colors = listOf(topColor, bottomColor),
        startY = center.y - r,
        endY = center.y + r
    )
    drawPath(path = facePath, brush = faceBrush, style = Fill)

    // 4. Glossy Specular Arc (Sheen on top half)
    val sheenPath = Path().apply {
        val deg240 = Math.toRadians(240.0)
        val deg300 = Math.toRadians(300.0)
        val xTL = center.x + (r - 2f) * cos(deg240).toFloat()
        val yTL = center.y + (r - 2f) * sin(deg240).toFloat()
        val xTR = center.x + (r - 2f) * cos(deg300).toFloat()
        val yTR = center.y + (r - 2f) * sin(deg300).toFloat()

        moveTo(xTL, yTL)
        lineTo(xTR, yTR)
        quadraticBezierTo(
            center.x,
            center.y - r * 0.05f,
            xTL,
            yTL
        )
        close()
    }
    drawPath(
        path = sheenPath,
        color = Color.White.copy(alpha = 0.38f),
        style = Fill
    )

    // 5. Crisp Rim Light on top edges
    val rimPath = Path().apply {
        val angles = listOf(180.0, 240.0, 300.0, 0.0)
        for (i in angles.indices) {
            val rad = Math.toRadians(angles[i])
            val x = center.x + r * cos(rad).toFloat()
            val y = center.y + r * sin(rad).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
    }
    drawPath(
        path = rimPath,
        color = Color.White.copy(alpha = 0.65f),
        style = Stroke(width = 2.5f)
    )

    // Outer subtle border
    val borderColor = if (isRecentlyMerged) Color.White else Color.White.copy(alpha = 0.28f)
    drawPath(path = facePath, color = borderColor, style = Stroke(width = 1.8f))

    // 6. Number in Center (Pure white bold with dark drop shadow)
    val textSize = r * 0.98f
    val paint = Paint().apply {
        color = Color.White.toArgb()
        this.textSize = textSize
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(4.5f, 0f, 2.5f, 0xAA000000.toInt())
    }
    drawIntoCanvas { canvas ->
        val yOffset = (paint.descent() + paint.ascent()) / 2f
        canvas.nativeCanvas.drawText(numberText, center.x, center.y - yOffset, paint)
    }
}

fun DrawScope.drawTextOnHex(
    text: String,
    center: Offset,
    textSize: Float,
    textColor: Int
) {
    val paint = Paint().apply {
        color = textColor
        this.textSize = textSize
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(4f, 0f, 2f, 0x99000000.toInt())
    }
    drawIntoCanvas { canvas ->
        val yOffset = (paint.descent() + paint.ascent()) / 2f
        canvas.nativeCanvas.drawText(text, center.x, center.y - yOffset, paint)
    }
}

fun DrawScope.drawStar(center: Offset, size: Float, color: Color) {
    val path = Path()
    val points = 4
    val outerR = size
    val innerR = size * 0.35f
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outerR else innerR
        val angle = i * PI / points
        val x = center.x + r * cos(angle).toFloat()
        val y = center.y + r * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color, style = Fill)
}
