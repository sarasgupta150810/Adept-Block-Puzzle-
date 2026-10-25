package com.adept.blockpuzzle.game

import com.adept.blockpuzzle.model.CellData
import com.adept.blockpuzzle.model.HexCoord
import com.adept.blockpuzzle.model.HexPiece
import com.adept.blockpuzzle.model.LevelTarget

data class PlaceResult(
    val newBoard: Map<HexCoord, CellData>,
    val pointsScored: Int,
    val linesClearedCount: Int,
    val clearedCoords: Set<HexCoord>,
    val evolvedCoords: Map<HexCoord, Int>, // coord to newTier
    val highestTierReached: Int,
    val comboMultiplier: Int,
    val newComboStreak: Int,
    val hadSuccessfulMerge: Boolean,
    val isLevelComplete: Boolean,
    val isGameOver: Boolean
)

object GameEngine {

    fun placePiece(
        piece: HexPiece,
        anchor: HexCoord,
        currentBoard: Map<HexCoord, CellData>,
        currentScore: Int,
        highestTierOnBoard: Int,
        totalLinesCleared: Int,
        remainingPiecesInTray: List<HexPiece>,
        currentComboStreak: Int = 0,
        levelTarget: LevelTarget? = null
    ): PlaceResult {
        val board = currentBoard.toMutableMap()
        var points = piece.coords.size * 10
        var linesClearedCount = 0
        val allClearedCoords = mutableSetOf<HexCoord>()
        val evolvedCoords = mutableMapOf<HexCoord, Int>()
        var highestTier = highestTierOnBoard

        // 1. Place the piece cells
        for (i in piece.coords.indices) {
            val target = anchor + piece.coords[i]
            val value = piece.values.getOrElse(i) { piece.baseValue }
            board[target] = CellData(value = value)
            if (value > highestTier) highestTier = value
        }

        // 2. Cascade Evolution merges & Consecutive Combo Multiplier
        var hadSuccessfulMerge = false
        var cascadeCount = 0
        var streak = currentComboStreak

        var hadEvolutionInStep = true
        while (hadEvolutionInStep) {
            hadEvolutionInStep = false
            val clusters = findMatchingClusters(board)
            val mergeCluster = clusters.firstOrNull { it.size >= 3 }
            if (mergeCluster != null) {
                if (!hadSuccessfulMerge) {
                    hadSuccessfulMerge = true
                    streak = currentComboStreak + 1
                }
                cascadeCount++
                hadEvolutionInStep = true

                val valToMerge = board[mergeCluster.first()]?.value ?: 1
                val newTier = valToMerge + 1
                if (newTier > highestTier) highestTier = newTier

                // Find best merge destination cell (closest to placement anchor)
                val destCoord = mergeCluster.minByOrNull { it.distanceTo(anchor) } ?: mergeCluster.first()

                // Clear other cells in cluster
                for (coord in mergeCluster) {
                    if (coord == destCoord) {
                        board[coord] = CellData(value = newTier, isRecentlyMerged = true)
                        evolvedCoords[coord] = newTier
                    } else {
                        board.remove(coord)
                        allClearedCoords.add(coord)
                    }
                }

                // Combo multiplier scales with consecutive successful move streak + cascade level:
                // Move 1 with merge: 1x (or 2x on cascade)
                // Move 2 with merge: 2x (or 3x on cascade)
                // Move 3 with merge: 3x (or 4x on cascade)
                val activeMultiplier = maxOf(1, streak + (cascadeCount - 1))
                val evolutionPoints = valToMerge * 100 * mergeCluster.size * activeMultiplier
                points += evolutionPoints
            }
        }

        // If this move had no merges, streak resets to 0
        val finalComboStreak = if (hadSuccessfulMerge) streak else 0
        val finalMultiplier = if (hadSuccessfulMerge) maxOf(1, streak + (cascadeCount - 1)) else 1

        // 3. Check for Full Lines
        val linesToClear = mutableListOf<List<HexCoord>>()
        for (line in HexCoord.ALL_GRID_LINES) {
            val isFull = line.all { coord ->
                val cell = board[coord]
                cell != null && cell.value > 0 // occupied by standard block
            }
            if (isFull) {
                linesToClear.add(line)
            }
        }

        if (linesToClear.isNotEmpty()) {
            linesClearedCount = linesToClear.size
            val cellsToClearFromLines = linesToClear.flatten().toSet()
            for (coord in cellsToClearFromLines) {
                val cell = board[coord]
                if (cell != null && !cell.isLocked) {
                    board.remove(coord)
                    allClearedCoords.add(coord)
                }
            }

            val linePoints = cellsToClearFromLines.size * 50 * linesClearedCount * finalMultiplier
            points += linePoints
        }

        val newTotalScore = currentScore + points
        val newTotalLines = totalLinesCleared + linesClearedCount

        // 4. Check Level Complete (in Challenge mode)
        var levelComplete = false
        if (levelTarget != null) {
            val tierOk = levelTarget.targetEvolutionTier == 0 || highestTier >= levelTarget.targetEvolutionTier
            val linesOk = levelTarget.targetLinesToClear == 0 || newTotalLines >= levelTarget.targetLinesToClear
            val scoreOk = newTotalScore >= levelTarget.targetScore
            if (tierOk && linesOk && scoreOk) {
                levelComplete = true
            }
        }

        // 5. Check Game Over
        var gameOver = false
        val trayAfterPlacement = remainingPiecesInTray.filter { it.id != piece.id }
        if (trayAfterPlacement.isNotEmpty()) {
            val canAnyFit = trayAfterPlacement.any { HexPieceGenerator.canPieceFitAnywhere(it, board) }
            if (!canAnyFit && !levelComplete) {
                gameOver = true
            }
        }

        return PlaceResult(
            newBoard = board,
            pointsScored = points,
            linesClearedCount = linesClearedCount,
            clearedCoords = allClearedCoords,
            evolvedCoords = evolvedCoords,
            highestTierReached = highestTier,
            comboMultiplier = finalMultiplier,
            newComboStreak = finalComboStreak,
            hadSuccessfulMerge = hadSuccessfulMerge,
            isLevelComplete = levelComplete,
            isGameOver = gameOver
        )
    }

    /**
     * Finds connected clusters of identical values >= 1.
     */
    private fun findMatchingClusters(board: Map<HexCoord, CellData>): List<Set<HexCoord>> {
        val visited = mutableSetOf<HexCoord>()
        val clusters = mutableListOf<Set<HexCoord>>()

        for ((coord, cell) in board) {
            if (cell.value <= 0 || cell.isLocked || coord in visited) continue

            val cluster = mutableSetOf<HexCoord>()
            val queue = ArrayDeque<HexCoord>()
            queue.add(coord)
            visited.add(coord)

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                cluster.add(current)

                for (neighbor in current.neighbors()) {
                    if (neighbor in visited) continue
                    val neighborCell = board[neighbor]
                    if (neighborCell != null && neighborCell.value == cell.value && !neighborCell.isLocked) {
                        visited.add(neighbor)
                        queue.add(neighbor)
                    }
                }
            }

            if (cluster.size >= 3) {
                clusters.add(cluster)
            }
        }

        return clusters
    }
}
