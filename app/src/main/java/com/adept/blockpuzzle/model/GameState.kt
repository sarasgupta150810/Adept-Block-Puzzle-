package com.adept.blockpuzzle.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class CellData(
    val value: Int, // 1, 2, 3, 4, 5, 6, 7, etc. (0 = empty, -1 = obstacle/crystal)
    val isLocked: Boolean = false,
    val isRecentlyMerged: Boolean = false,
    val isRecentlyCleared: Boolean = false,
    val highlightAlpha: Float = 0f
)

data class FloatingText(
    val id: Long,
    val text: String,
    val position: Offset,
    val color: Color,
    val sizeSp: Float = 22f,
    val progress: Float = 0f // 0f -> 1f as it rises and fades
)

data class Particle(
    val id: Long,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val life: Float, // 1f down to 0f
    val isStar: Boolean = false
)

enum class GameMode {
    CLASSIC,
    CHALLENGE,
    DAILY_CHALLENGE
}

enum class GameStatus {
    PLAYING,
    PAUSED,
    LEVEL_COMPLETE,
    GAME_OVER
}

data class LevelTarget(
    val levelNumber: Int,
    val title: String,
    val description: String,
    val targetScore: Int,
    val targetEvolutionTier: Int = 0, // e.g. Reach Tier 4, 5, 6...
    val targetLinesToClear: Int = 0,   // e.g. Clear 3 lines
    val initialBoard: Map<HexCoord, CellData> = emptyMap(),
    val maxMoves: Int = 0, // 0 = unlimited moves
    val isDaily: Boolean = false,
    val dailyDateKey: String = "",
    val rewardBadge: String = "Crown 👑"
)
