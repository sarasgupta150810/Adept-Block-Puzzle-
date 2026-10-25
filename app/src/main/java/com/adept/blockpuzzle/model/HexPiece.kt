package com.adept.blockpuzzle.model

import java.util.UUID

/**
 * A polyhex piece composed of multiple flat-topped hexagon blocks relative to (0,0).
 */
data class HexPiece(
    val id: String = UUID.randomUUID().toString(),
    val coords: List<HexCoord>,
    val values: List<Int>,
    val baseValue: Int = values.firstOrNull() ?: 1
) {
    val size: Int get() = coords.size

    fun getBoundingRadius(): Int {
        return coords.maxOfOrNull { it.distanceToCenter() } ?: 0
    }

    companion object {
        val SHAPE_SINGLE = listOf(HexCoord(0, 0))

        // 2-cell dominoes
        val SHAPE_DOMINO_V = listOf(HexCoord(0, 0), HexCoord(0, 1))
        val SHAPE_DOMINO_D1 = listOf(HexCoord(0, 0), HexCoord(1, 0))
        val SHAPE_DOMINO_D2 = listOf(HexCoord(0, 0), HexCoord(1, -1))

        // 3-cell lines
        val SHAPE_TRI_LINE_V = listOf(HexCoord(0, -1), HexCoord(0, 0), HexCoord(0, 1))
        val SHAPE_TRI_LINE_D1 = listOf(HexCoord(-1, 0), HexCoord(0, 0), HexCoord(1, 0))
        val SHAPE_TRI_LINE_D2 = listOf(HexCoord(-1, 1), HexCoord(0, 0), HexCoord(1, -1))

        // 3-cell compact triangles
        val SHAPE_TRIANGLE = listOf(HexCoord(0, 0), HexCoord(1, 0), HexCoord(0, 1))
        val SHAPE_TRIANGLE_INV = listOf(HexCoord(0, 0), HexCoord(-1, 0), HexCoord(0, -1))

        // 3-cell corners / bends
        val SHAPE_BEND_1 = listOf(HexCoord(0, -1), HexCoord(0, 0), HexCoord(1, 0))
        val SHAPE_BEND_2 = listOf(HexCoord(0, 1), HexCoord(0, 0), HexCoord(-1, 1))
        val SHAPE_BEND_3 = listOf(HexCoord(-1, 0), HexCoord(0, 0), HexCoord(0, 1))
        val SHAPE_BEND_4 = listOf(HexCoord(1, -1), HexCoord(0, 0), HexCoord(0, -1))

        // 4-cell shapes
        val SHAPE_TETRA_LINE_V = listOf(HexCoord(0, -1), HexCoord(0, 0), HexCoord(0, 1), HexCoord(0, 2))
        val SHAPE_TETRA_LINE_D1 = listOf(HexCoord(-1, 0), HexCoord(0, 0), HexCoord(1, 0), HexCoord(2, 0))
        val SHAPE_RHOMBUS = listOf(HexCoord(0, 0), HexCoord(1, 0), HexCoord(0, 1), HexCoord(1, 1))
        val SHAPE_Y_TRIPOD = listOf(HexCoord(0, 0), HexCoord(0, -1), HexCoord(1, 0), HexCoord(-1, 1))
        val SHAPE_WORM_1 = listOf(HexCoord(0, -1), HexCoord(0, 0), HexCoord(1, 0), HexCoord(1, 1))
        val SHAPE_WORM_2 = listOf(HexCoord(-1, 0), HexCoord(0, 0), HexCoord(0, 1), HexCoord(1, 1))

        val ALL_SHAPES = listOf(
            SHAPE_SINGLE,
            SHAPE_DOMINO_V, SHAPE_DOMINO_D1, SHAPE_DOMINO_D2,
            SHAPE_TRI_LINE_V, SHAPE_TRI_LINE_D1, SHAPE_TRI_LINE_D2,
            SHAPE_TRIANGLE, SHAPE_TRIANGLE_INV,
            SHAPE_BEND_1, SHAPE_BEND_2, SHAPE_BEND_3, SHAPE_BEND_4,
            SHAPE_RHOMBUS,
            SHAPE_Y_TRIPOD,
            SHAPE_WORM_1, SHAPE_WORM_2,
            SHAPE_TETRA_LINE_V, SHAPE_TETRA_LINE_D1
        )
    }
}
