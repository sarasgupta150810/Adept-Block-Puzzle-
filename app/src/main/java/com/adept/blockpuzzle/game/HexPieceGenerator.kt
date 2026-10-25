package com.adept.blockpuzzle.game

import com.adept.blockpuzzle.model.CellData
import com.adept.blockpuzzle.model.HexCoord
import com.adept.blockpuzzle.model.HexPiece
import kotlin.random.Random

object HexPieceGenerator {

    fun generatePieces(
        board: Map<HexCoord, CellData>,
        highestTierOnBoard: Int,
        count: Int = 3
    ): List<HexPiece> {
        val pieces = mutableListOf<HexPiece>()
        val maxPieceTier = (highestTierOnBoard - 1).coerceIn(1, 6)

        for (i in 0 until count) {
            val shape = HexPiece.ALL_SHAPES.random()
            // Random tier: 65% chance of tier 1..2, 25% chance of tier 3, 10% chance of maxPieceTier
            val tier = when (Random.nextFloat()) {
                in 0f..0.50f -> 1
                in 0.50f..0.80f -> (2).coerceAtMost(maxPieceTier)
                in 0.80f..0.95f -> (3).coerceAtMost(maxPieceTier)
                else -> maxPieceTier
            }

            // Assign values to cells of the shape (uniform or subtle variation)
            val values = List(shape.size) { tier }
            pieces.add(
                HexPiece(
                    coords = shape,
                    values = values,
                    baseValue = tier
                )
            )
        }

        // Check if any of the pieces can fit on board. If none can fit, replace the first with a single cell!
        val canAnyFit = pieces.any { canPieceFitAnywhere(it, board) }
        if (!canAnyFit && pieces.isNotEmpty()) {
            val tier = 1.coerceAtMost(maxPieceTier)
            pieces[0] = HexPiece(
                coords = HexPiece.SHAPE_SINGLE,
                values = listOf(tier),
                baseValue = tier
            )
        }

        return pieces
    }

    fun canPieceFitAnywhere(piece: HexPiece, board: Map<HexCoord, CellData>): Boolean {
        for (anchor in HexCoord.ALL_GRID_CELLS) {
            if (canPlacePiece(piece, anchor, board)) {
                return true
            }
        }
        return false
    }

    fun canPlacePiece(piece: HexPiece, anchor: HexCoord, board: Map<HexCoord, CellData>): Boolean {
        for (offset in piece.coords) {
            val target = anchor + offset
            // Must be within grid bounds
            if (target.distanceToCenter() > HexCoord.GRID_RADIUS) {
                return false
            }
            // Must not be already occupied or locked
            val cell = board[target]
            if (cell != null && cell.value != 0) {
                return false
            }
        }
        return true
    }
}
