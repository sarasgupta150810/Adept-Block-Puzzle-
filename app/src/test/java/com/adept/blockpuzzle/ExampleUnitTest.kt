package com.adept.blockpuzzle

import com.adept.blockpuzzle.game.GameEngine
import com.adept.blockpuzzle.game.HexPieceGenerator
import com.adept.blockpuzzle.game.LevelCatalog
import com.adept.blockpuzzle.model.CellData
import com.adept.blockpuzzle.model.HexCoord
import com.adept.blockpuzzle.model.HexPiece
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testHexDistanceAndNeighbors() {
        val center = HexCoord(0, 0)
        assertEquals(0, center.distanceToCenter())

        val neighbor = HexCoord(1, 0)
        assertEquals(1, neighbor.distanceToCenter())
        assertEquals(1, center.distanceTo(neighbor))

        val neighbors = center.neighbors()
        assertEquals(6, neighbors.size)
        assertTrue(neighbors.all { it.distanceToCenter() == 1 })
    }

    @Test
    fun testHexGridLinesCount() {
        // Radius 4 pointy-topped hex grid should contain 61 cells and 27 lines
        assertEquals(61, HexCoord.ALL_GRID_CELLS.size)
        assertEquals(27, HexCoord.ALL_GRID_LINES.size)
    }

    @Test
    fun testPiecePlacementAndFitting() {
        val singlePiece = HexPiece(
            coords = HexPiece.SHAPE_SINGLE,
            values = listOf(1),
            baseValue = 1
        )
        val emptyBoard = emptyMap<HexCoord, CellData>()

        // Can place at center
        assertTrue(HexPieceGenerator.canPlacePiece(singlePiece, HexCoord(0, 0), emptyBoard))

        // Cannot place outside radius
        assertFalse(HexPieceGenerator.canPlacePiece(singlePiece, HexCoord(5, 0), emptyBoard))

        // Cannot place on occupied cell
        val occupiedBoard = mapOf(HexCoord(0, 0) to CellData(value = 2))
        assertFalse(HexPieceGenerator.canPlacePiece(singlePiece, HexCoord(0, 0), occupiedBoard))
    }

    @Test
    fun testEvolutionMergeMechanic() {
        // Place two '2's, then place a third '2' adjacent to them.
        // They should evolve into a '3'!
        val board = mutableMapOf(
            HexCoord(0, 0) to CellData(value = 2),
            HexCoord(1, 0) to CellData(value = 2)
        )

        val newPiece = HexPiece(
            coords = listOf(HexCoord(0, 0)),
            values = listOf(2),
            baseValue = 2
        )

        val result = GameEngine.placePiece(
            piece = newPiece,
            anchor = HexCoord(0, 1),
            currentBoard = board,
            currentScore = 0,
            highestTierOnBoard = 2,
            totalLinesCleared = 0,
            remainingPiecesInTray = emptyList()
        )

        // Cluster should merge into a 3!
        val cellValues = result.newBoard.values.map { it.value }
        assertTrue("Board should contain an evolved 3", cellValues.contains(3))
        assertEquals(1, cellValues.size) // 3 cells merged into 1
        assertEquals(3, result.highestTierReached)
        assertTrue("Should have awarded points for evolution", result.pointsScored > 0)
    }

    @Test
    fun testLevelCatalogStars() {
        val level1 = LevelCatalog.getLevel(1)
        assertEquals(1, level1.levelNumber)

        // Base completion -> 1 star
        val stars1 = LevelCatalog.calculateStars(level1, score = 250, highestTier = 2, linesCleared = 0)
        assertEquals(1, stars1)

        // High score -> 3 stars
        val stars3 = LevelCatalog.calculateStars(level1, score = 800, highestTier = 2, linesCleared = 0)
        assertEquals(3, stars3)
    }

    @Test
    fun testConsecutiveMergeComboSystem() {
        // Move 1: Place a block that merges three 1s
        val board1 = mutableMapOf(
            HexCoord(0, 0) to CellData(value = 1),
            HexCoord(1, 0) to CellData(value = 1)
        )
        val piece1 = HexPiece(
            coords = listOf(HexCoord(0, 0)),
            values = listOf(1),
            baseValue = 1
        )
        val result1 = GameEngine.placePiece(
            piece = piece1,
            anchor = HexCoord(0, 1),
            currentBoard = board1,
            currentScore = 0,
            highestTierOnBoard = 1,
            totalLinesCleared = 0,
            remainingPiecesInTray = emptyList(),
            currentComboStreak = 0
        )
        assertTrue(result1.hadSuccessfulMerge)
        assertEquals(1, result1.newComboStreak)
        assertEquals(1, result1.comboMultiplier)

        // Move 2: Consecutive move that merges three 2s with currentComboStreak = 1
        val board2 = result1.newBoard.toMutableMap()
        // Setup two more 2s adjacent to the new 2 formed at (0, 1)
        board2[HexCoord(1, 1)] = CellData(value = 2)
        board2[HexCoord(0, 2)] = CellData(value = 2)

        val piece2 = HexPiece(
            coords = listOf(HexCoord(0, 0)),
            values = listOf(2),
            baseValue = 2
        )
        val result2 = GameEngine.placePiece(
            piece = piece2,
            anchor = HexCoord(-1, 2),
            currentBoard = board2,
            currentScore = result1.pointsScored,
            highestTierOnBoard = 2,
            totalLinesCleared = 0,
            remainingPiecesInTray = emptyList(),
            currentComboStreak = result1.newComboStreak
        )
        assertTrue(result2.hadSuccessfulMerge)
        assertEquals(2, result2.newComboStreak)
        assertEquals(2, result2.comboMultiplier)
        // Score for 2x combo should be doubled compared to base 1x
        assertTrue(result2.pointsScored > result1.pointsScored)

        // Move 3: A move that places a single block with NO merges
        val nonMergingPiece = HexPiece(
            coords = listOf(HexCoord(0, 0)),
            values = listOf(9),
            baseValue = 9
        )
        val result3 = GameEngine.placePiece(
            piece = nonMergingPiece,
            anchor = HexCoord(-3, 0),
            currentBoard = result2.newBoard,
            currentScore = result2.pointsScored,
            highestTierOnBoard = 3,
            totalLinesCleared = 0,
            remainingPiecesInTray = emptyList(),
            currentComboStreak = result2.newComboStreak
        )
        assertFalse(result3.hadSuccessfulMerge)
        // Consecutive streak resets back to 0!
        assertEquals(0, result3.newComboStreak)
        assertEquals(1, result3.comboMultiplier)
    }
}
