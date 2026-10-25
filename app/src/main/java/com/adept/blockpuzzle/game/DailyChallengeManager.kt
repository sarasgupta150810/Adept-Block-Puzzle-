package com.adept.blockpuzzle.game

import com.adept.blockpuzzle.model.CellData
import com.adept.blockpuzzle.model.HexCoord
import com.adept.blockpuzzle.model.LevelTarget
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

object DailyChallengeManager {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val DISPLAY_DATE_FORMAT = SimpleDateFormat("MMM d, yyyy", Locale.US)

    fun getTodayDateKey(): String {
        return DATE_FORMAT.format(Calendar.getInstance().time)
    }

    fun getTodayDisplayDate(): String {
        return DISPLAY_DATE_FORMAT.format(Calendar.getInstance().time)
    }

    fun getYesterdayDateKey(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return DATE_FORMAT.format(cal.time)
    }

    /**
     * Generates a deterministic daily puzzle challenge for a given date.
     */
    fun getDailyChallenge(dateKey: String = getTodayDateKey()): LevelTarget {
        val seed = abs(dateKey.hashCode().toLong()) * 31337L + 7L
        val rand = Random(seed)

        val cal = Calendar.getInstance()
        try {
            val date = DATE_FORMAT.parse(dateKey)
            if (date != null) cal.time = date
        } catch (_: Exception) {}

        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

        val targetTier = if (isWeekend) 6 else 4
        val targetLines = if (isWeekend) 5 else 2
        val targetScore = if (isWeekend) 16000 else 4500

        val titles = listOf(
            "Cosmic Crucible", "Starlight Matrix", "Prismatic Nexus",
            "Solar Eclipse", "Quantum Resonance", "Mystic Horizon",
            "Aura Vortex", "Titan Gateway", "Eternity Pulse"
        )
        val dailyTitle = "${titles[rand.nextInt(titles.size)]} ($dateKey)"

        // Distinct symmetrical puzzle layouts rotated deterministically
        val initialBoard = mutableMapOf<HexCoord, CellData>()
        val patternIndex = abs(dateKey.hashCode()) % 6

        when (patternIndex) {
            0 -> {
                // Twin Pillars
                initialBoard[HexCoord(-2, 0)] = CellData(value = -1, isLocked = true)
                initialBoard[HexCoord(2, 0)] = CellData(value = -1, isLocked = true)
            }
            1 -> {
                // Center Crystal + Starter Seeds
                initialBoard[HexCoord(0, 0)] = CellData(value = -1, isLocked = true)
                initialBoard[HexCoord(-1, 0)] = CellData(value = 3)
                initialBoard[HexCoord(1, 0)] = CellData(value = 3)
            }
            2 -> {
                // Catalyst Trio (pre-placed numbered tiles ready for chain merge)
                initialBoard[HexCoord(0, -2)] = CellData(value = 2)
                initialBoard[HexCoord(0, 2)] = CellData(value = 2)
                initialBoard[HexCoord(-1, 1)] = CellData(value = 4)
                initialBoard[HexCoord(1, -1)] = CellData(value = 4)
            }
            3 -> {
                // Symmetrical Gateways
                initialBoard[HexCoord(-1, -1)] = CellData(value = -1, isLocked = true)
                initialBoard[HexCoord(1, 1)] = CellData(value = -1, isLocked = true)
            }
            4 -> {
                // Cross Guardians
                initialBoard[HexCoord(0, -1)] = CellData(value = -1, isLocked = true)
                initialBoard[HexCoord(0, 1)] = CellData(value = -1, isLocked = true)
            }
            else -> {
                // Open Field with Seed Catalysts
                initialBoard[HexCoord(-1, 0)] = CellData(value = 2)
                initialBoard[HexCoord(1, 0)] = CellData(value = 2)
                initialBoard[HexCoord(0, -1)] = CellData(value = 3)
                initialBoard[HexCoord(0, 1)] = CellData(value = 3)
            }
        }

        return LevelTarget(
            levelNumber = 9999, // Special daily indicator
            title = dailyTitle,
            description = "Evolve to Tier $targetTier, clear $targetLines lines, and claim today's Crown 👑!",
            targetScore = targetScore,
            targetEvolutionTier = targetTier,
            targetLinesToClear = targetLines,
            initialBoard = initialBoard,
            isDaily = true,
            dailyDateKey = dateKey,
            rewardBadge = "Daily Crown 👑"
        )
    }

    /**
     * Calculates the new daily streak based on the previous completion date.
     */
    fun computeNewStreak(todayKey: String, lastCompletedKey: String?, previousStreak: Int): Int {
        if (lastCompletedKey == null) return 1
        if (lastCompletedKey == todayKey) return previousStreak // already done today

        val yesterdayKey = getYesterdayDateKey()
        return if (lastCompletedKey == yesterdayKey) {
            previousStreak + 1
        } else {
            1
        }
    }
}
