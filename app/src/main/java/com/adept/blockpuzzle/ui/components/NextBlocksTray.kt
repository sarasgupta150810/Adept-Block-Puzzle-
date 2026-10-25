package com.adept.blockpuzzle.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adept.blockpuzzle.model.HexPiece
import com.adept.blockpuzzle.ui.theme.HexColors

@Composable
fun NextBlocksTray(
    modifier: Modifier = Modifier,
    pieces: List<HexPiece>,
    draggedPiece: HexPiece?,
    selectedPiece: HexPiece?,
    onSelectPiece: (HexPiece?) -> Unit,
    onDragStart: (HexPiece, Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "NEXT BLOCKS" banner
        Text(
            text = "NEXT BLOCKS",
            color = Color(0xFF1E293B),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Dock background (Matching Panel 2 dock container)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(HexColors.TrayBackground, Color(0xFF1B283E))
                    )
                )
                .border(2.5.dp, HexColors.TrayBorder, RoundedCornerShape(20.dp))
                .padding(8.dp)
                .testTag("next_blocks_dock")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 3) {
                    val piece = pieces.getOrNull(i)
                    PieceSlot(
                        piece = piece,
                        isBeingDragged = piece != null && piece.id == draggedPiece?.id,
                        isSelected = piece != null && piece.id == selectedPiece?.id,
                        onSelect = {
                            if (piece != null) {
                                if (selectedPiece?.id == piece.id) {
                                    onSelectPiece(null)
                                } else {
                                    onSelectPiece(piece)
                                }
                            }
                        },
                        onDragStart = onDragStart,
                        onDragMove = onDragMove,
                        onDragEnd = onDragEnd,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PieceSlot(
    modifier: Modifier = Modifier,
    piece: HexPiece?,
    isBeingDragged: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDragStart: (HexPiece, Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    var slotPositionInRoot by remember { mutableStateOf(Offset.Zero) }

    val scale by animateFloatAsState(
        targetValue = if (isSelected && !isBeingDragged) 1.06f else 1.0f,
        animationSpec = tween(150),
        label = "slot_scale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) HexColors.AccentGreenBright else Color(0x33FFFFFF),
        animationSpec = tween(200),
        label = "border_color"
    )

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .height(94.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(HexColors.TraySlotBackground)
            .border(
                width = if (isSelected) 2.5.dp else 1.2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = piece != null && !isBeingDragged) { onSelect() }
            .onGloballyPositioned { coordinates ->
                val rootPos = coordinates.positionInRoot()
                val w = coordinates.size.width.toFloat()
                val h = coordinates.size.height.toFloat()
                slotPositionInRoot = Offset(rootPos.x + w / 2f, rootPos.y + h / 2f)
            }
            .pointerInput(piece) {
                if (piece != null) {
                    var currentTouchInRoot = slotPositionInRoot
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            currentTouchInRoot = slotPositionInRoot + startOffset
                            onDragStart(piece, currentTouchInRoot)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            currentTouchInRoot += dragAmount
                            onDragMove(currentTouchInRoot)
                        },
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragEnd
                    )
                }
            }
            .testTag(if (piece != null) "piece_slot_${piece.id}" else "piece_slot_empty"),
        contentAlignment = Alignment.Center
    ) {
        if (piece != null) {
            // When piece is actively being dragged, show faint ghost in the slot
            val alpha = if (isBeingDragged) 0.25f else 1.0f
            PieceMiniCanvas(piece = piece, alpha = alpha)
        }
    }
}

@Composable
fun PieceMiniCanvas(
    piece: HexPiece,
    modifier: Modifier = Modifier,
    alpha: Float = 1.0f
) {
    Canvas(modifier = modifier.fillMaxWidth().height(84.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)

        val maxDist = piece.getBoundingRadius().coerceAtLeast(1)
        val miniRadius = (16.5f - (maxDist - 1) * 2.5f).coerceIn(10.5f, 15.5f)

        // Center piece perfectly based on its bounding box centroid
        var avgX = 0f
        var avgY = 0f
        for (coord in piece.coords) {
            val offset = coord.toScreenOffset(miniRadius, Offset.Zero)
            avgX += offset.x
            avgY += offset.y
        }
        val centroid = Offset(avgX / piece.coords.size, avgY / piece.coords.size)
        val adjustedCenter = center - centroid

        for (i in piece.coords.indices) {
            val coord = piece.coords[i]
            val cellPos = coord.toScreenOffset(miniRadius, adjustedCenter)
            val value = piece.values.getOrElse(i) { piece.baseValue }
            val (c1, c2) = HexColors.getColorsForTier(value)

            drawProfessional3DHex(
                center = cellPos,
                radius = miniRadius - 1.5f,
                topColor = c1.copy(alpha = alpha),
                bottomColor = c2.copy(alpha = alpha),
                numberText = value.toString()
            )
        }
    }
}
