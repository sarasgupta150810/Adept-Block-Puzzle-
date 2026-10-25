package com.adept.blockpuzzle.model

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Axial coordinates (q, r) for FLAT-TOPPED hexagons.
 * Third cubic coordinate s = -q - r.
 * In flat-topped layout:
 * - Top and bottom edges are horizontal flat lines.
 * - Columns run vertically along constant q.
 * - Column -4 has 5 vertical cells, center Column 0 has 9 cells, Column +4 has 5 cells.
 */
data class HexCoord(val q: Int, val r: Int) {
    val s: Int get() = -q - r

    fun distanceToCenter(): Int = maxOf(abs(q), abs(r), abs(s))

    fun distanceTo(other: HexCoord): Int {
        val dq = q - other.q
        val dr = r - other.r
        val ds = s - other.s
        return maxOf(abs(dq), abs(dr), abs(ds))
    }

    operator fun plus(other: HexCoord) = HexCoord(q + other.q, r + other.r)
    operator fun minus(other: HexCoord) = HexCoord(q - other.q, r - other.r)

    /**
     * 6 Neighbors for flat-topped hexagon.
     */
    fun neighbors(): List<HexCoord> = listOf(
        HexCoord(q, r - 1),       // Up
        HexCoord(q + 1, r - 1),   // Up-Right
        HexCoord(q + 1, r),       // Down-Right
        HexCoord(q, r + 1),       // Down
        HexCoord(q - 1, r + 1),   // Down-Left
        HexCoord(q - 1, r)        // Up-Left
    )

    /**
     * Converts flat-topped hex coordinates to 2D screen offset.
     * x = center.x + hexRadius * 1.5 * q
     * y = center.y + hexRadius * sqrt(3) * (r + q / 2)
     */
    fun toScreenOffset(hexRadius: Float, center: Offset): Offset {
        val x = center.x + hexRadius * 1.5f * q
        val y = center.y + hexRadius * sqrt(3.0f) * (r + q / 2.0f)
        return Offset(x, y)
    }

    companion object {
        const val GRID_RADIUS = 4 // Total 61 cells, 9 columns (5, 6, 7, 8, 9, 8, 7, 6, 5)

        /**
         * Converts screen pixel position (relative to center) to closest flat-topped HexCoord.
         */
        fun fromScreenOffset(pixel: Offset, center: Offset, hexRadius: Float): HexCoord {
            val px = pixel.x - center.x
            val py = pixel.y - center.y

            val fracQ = (2.0f / 3.0f * px) / hexRadius
            val fracR = (-1.0f / 3.0f * px + sqrt(3.0f) / 3.0f * py) / hexRadius
            val fracS = -fracQ - fracR

            var qRound = fracQ.roundToInt()
            var rRound = fracR.roundToInt()
            var sRound = fracS.roundToInt()

            val qDiff = abs(qRound - fracQ)
            val rDiff = abs(rRound - fracR)
            val sDiff = abs(sRound - fracS)

            if (qDiff > rDiff && qDiff > sDiff) {
                qRound = -rRound - sRound
            } else if (rDiff > sDiff) {
                rRound = -qRound - sRound
            } else {
                sRound = -qRound - rRound
            }
            return HexCoord(qRound, rRound)
        }

        /**
         * Returns all 61 hex cells in the grid.
         */
        val ALL_GRID_CELLS: List<HexCoord> by lazy {
            val list = mutableListOf<HexCoord>()
            for (q in -GRID_RADIUS..GRID_RADIUS) {
                val r1 = maxOf(-GRID_RADIUS, -q - GRID_RADIUS)
                val r2 = minOf(GRID_RADIUS, -q + GRID_RADIUS)
                for (r in r1..r2) {
                    list.add(HexCoord(q, r))
                }
            }
            list
        }

        /**
         * Returns all 27 straight lines across 3 flat-topped hex directions:
         * 1) Vertical columns (constant q, -4..4)
         * 2) Diagonal 1 (constant r, -4..4)
         * 3) Diagonal 2 (constant s = -q-r, -4..4)
         */
        val ALL_GRID_LINES: List<List<HexCoord>> by lazy {
            val lines = mutableListOf<List<HexCoord>>()

            // 1. Vertical columns (constant q)
            for (q in -GRID_RADIUS..GRID_RADIUS) {
                val colCells = ALL_GRID_CELLS.filter { it.q == q }
                if (colCells.isNotEmpty()) lines.add(colCells)
            }

            // 2. Diagonal 1 (constant r)
            for (r in -GRID_RADIUS..GRID_RADIUS) {
                val diagCells = ALL_GRID_CELLS.filter { it.r == r }
                if (diagCells.isNotEmpty()) lines.add(diagCells)
            }

            // 3. Diagonal 2 (constant s = -q-r)
            for (s in -GRID_RADIUS..GRID_RADIUS) {
                val diagCells = ALL_GRID_CELLS.filter { it.s == s }
                if (diagCells.isNotEmpty()) lines.add(diagCells)
            }

            lines
        }
    }
}
