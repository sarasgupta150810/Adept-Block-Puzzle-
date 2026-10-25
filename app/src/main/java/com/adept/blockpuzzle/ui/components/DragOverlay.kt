package com.adept.blockpuzzle.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.adept.blockpuzzle.model.HexPiece
import com.adept.blockpuzzle.ui.theme.HexColors

@Composable
fun DragOverlay(
    modifier: Modifier = Modifier,
    draggedPiece: HexPiece?,
    dragPosition: Offset,
    hexRadius: Float,
    liftOffsetPx: Float
) {
    if (draggedPiece == null) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val pieceCenter = Offset(dragPosition.x, dragPosition.y - liftOffsetPx)

        // 1. Draw elevated floating drop shadow for each cell
        for (coord in draggedPiece.coords) {
            val cellPos = coord.toScreenOffset(hexRadius, pieceCenter)
            drawRoundedFlatHexagon(
                center = Offset(cellPos.x, cellPos.y + 14f),
                radius = hexRadius - 1f,
                cornerRadius = hexRadius * 0.16f,
                fillColor = Color(0x55000000)
            )
        }

        // 2. Draw glossy 3D candy cells of the piece at 1:1 scale
        for (i in draggedPiece.coords.indices) {
            val coord = draggedPiece.coords[i]
            val cellPos = coord.toScreenOffset(hexRadius, pieceCenter)
            val value = draggedPiece.values.getOrElse(i) { draggedPiece.baseValue }
            val (c1, c2) = HexColors.getColorsForTier(value)

            drawProfessional3DHex(
                center = cellPos,
                radius = hexRadius - 2f,
                topColor = c1,
                bottomColor = c2,
                numberText = value.toString(),
                isRecentlyMerged = true,
                pulseScale = 1.05f
            )
        }
    }
}
