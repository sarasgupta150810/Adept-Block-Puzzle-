package com.adept.blockpuzzle.game

import com.adept.blockpuzzle.model.CellData
import com.adept.blockpuzzle.model.HexCoord
import com.adept.blockpuzzle.model.LevelTarget
import kotlin.random.Random

object LevelCatalog {

    fun getLevel(levelNumber: Int): LevelTarget {
        if (levelNumber == 9999) {
            return DailyChallengeManager.getDailyChallenge()
        }
        return ALL_LEVELS.getOrNull(levelNumber - 1) ?: generateProceduralLevel(levelNumber)
    }

    val ALL_LEVELS: List<LevelTarget> = listOf(
        // === INTRODUCTORY & EASY STAGES (Levels 1 - 5) ===
        LevelTarget(
            levelNumber = 1,
            title = "First Steps",
            description = "Welcome! Merge two 1s to reach Tier 2 and score 250 points.",
            targetScore = 250,
            targetEvolutionTier = 2,
            targetLinesToClear = 0
        ),
        LevelTarget(
            levelNumber = 2,
            title = "First Line",
            description = "Fill any full hex line across the board to clear 1 line!",
            targetScore = 500,
            targetEvolutionTier = 0,
            targetLinesToClear = 1
        ),
        LevelTarget(
            levelNumber = 3,
            title = "Evolution Spark",
            description = "Keep merging numbers to synthesize a Tier 3 hexagon!",
            targetScore = 900,
            targetEvolutionTier = 3,
            targetLinesToClear = 0
        ),
        LevelTarget(
            levelNumber = 4,
            title = "Twin Lines",
            description = "Clear 2 lines in any hex direction and score 1,400 points.",
            targetScore = 1400,
            targetEvolutionTier = 0,
            targetLinesToClear = 2
        ),
        LevelTarget(
            levelNumber = 5,
            title = "Tier 3 Apprentice",
            description = "Reach Tier 3 and clear 1 line to complete the basic training!",
            targetScore = 2000,
            targetEvolutionTier = 3,
            targetLinesToClear = 1
        ),

        // === EARLY INTERMEDIATE (Levels 6 - 10) ===
        LevelTarget(
            levelNumber = 6,
            title = "Hexagon Flow",
            description = "Evolve matching blocks to reach Tier 4!",
            targetScore = 2800,
            targetEvolutionTier = 4,
            targetLinesToClear = 1
        ),
        LevelTarget(
            levelNumber = 7,
            title = "Triple Line Breaker",
            description = "Clear 3 lines and score 3,600 points across the board.",
            targetScore = 3600,
            targetEvolutionTier = 0,
            targetLinesToClear = 3
        ),
        LevelTarget(
            levelNumber = 8,
            title = "Center Crystal",
            description = "A crystal obstacle rests at center (0,0). Navigate around it!",
            targetScore = 4600,
            targetEvolutionTier = 4,
            targetLinesToClear = 2,
            initialBoard = mapOf(
                HexCoord(0, 0) to CellData(value = -1, isLocked = true)
            )
        ),
        LevelTarget(
            levelNumber = 9,
            title = "Catalyst Trio",
            description = "Pre-seeded starter blocks are ready for quick combos. Reach Tier 5!",
            targetScore = 6000,
            targetEvolutionTier = 5,
            targetLinesToClear = 2,
            initialBoard = mapOf(
                HexCoord(-1, 0) to CellData(value = 3),
                HexCoord(1, 0) to CellData(value = 3)
            )
        ),
        LevelTarget(
            levelNumber = 10,
            title = "Milestone: Tier 5",
            description = "Attain Tier 5 and clear 3 lines to achieve Level 10 mastery!",
            targetScore = 8000,
            targetEvolutionTier = 5,
            targetLinesToClear = 3
        ),

        // === MID-TIER CHALLENGES (Levels 11 - 15) ===
        LevelTarget(
            levelNumber = 11,
            title = "Twin Pillars",
            description = "Two crystal obstacles block outer columns. Clear 4 lines!",
            targetScore = 11000,
            targetEvolutionTier = 5,
            targetLinesToClear = 4,
            initialBoard = mapOf(
                HexCoord(-2, 0) to CellData(value = -1, isLocked = true),
                HexCoord(2, 0) to CellData(value = -1, isLocked = true)
            )
        ),
        LevelTarget(
            levelNumber = 12,
            title = "Line Dominator",
            description = "Clear 5 lines and score 14,000 points with chain combos!",
            targetScore = 14000,
            targetEvolutionTier = 0,
            targetLinesToClear = 5
        ),
        LevelTarget(
            levelNumber = 13,
            title = "Tier 6 Synthesis",
            description = "Forge an Electric Indigo Tier 6 hexagon and clear 4 lines.",
            targetScore = 18000,
            targetEvolutionTier = 6,
            targetLinesToClear = 4
        ),
        LevelTarget(
            levelNumber = 14,
            title = "Diagonal Barricade",
            description = "Obstacles line the diagonal axis. Reach Tier 6 and clear 5 lines!",
            targetScore = 24000,
            targetEvolutionTier = 6,
            targetLinesToClear = 5,
            initialBoard = mapOf(
                HexCoord(-2, 2) to CellData(value = -1, isLocked = true),
                HexCoord(0, 0) to CellData(value = -1, isLocked = true),
                HexCoord(2, -2) to CellData(value = -1, isLocked = true)
            )
        ),
        LevelTarget(
            levelNumber = 15,
            title = "Halfway Zenith",
            description = "Conquer Level 15: Forge a Tier 7 mega-hex and clear 6 lines!",
            targetScore = 32000,
            targetEvolutionTier = 7,
            targetLinesToClear = 6
        ),

        // === ADVANCED & HARD STAGES (Levels 16 - 20) ===
        LevelTarget(
            levelNumber = 16,
            title = "Crystal Cross",
            description = "Four obstacles guard the inner core. Score 42,000 and clear 7 lines!",
            targetScore = 42000,
            targetEvolutionTier = 7,
            targetLinesToClear = 7,
            initialBoard = mapOf(
                HexCoord(0, -1) to CellData(value = -1, isLocked = true),
                HexCoord(0, 1) to CellData(value = -1, isLocked = true),
                HexCoord(-1, 0) to CellData(value = -1, isLocked = true),
                HexCoord(1, 0) to CellData(value = -1, isLocked = true)
            )
        ),
        LevelTarget(
            levelNumber = 17,
            title = "Tier 8 Emerald",
            description = "Attain the legendary Tier 8 Mint Emerald with 52,000 points!",
            targetScore = 52000,
            targetEvolutionTier = 8,
            targetLinesToClear = 7
        ),
        LevelTarget(
            levelNumber = 18,
            title = "Crystal Fortress",
            description = "6 surrounding obstacle crystals. Clear 8 lines and reach Tier 8!",
            targetScore = 64000,
            targetEvolutionTier = 8,
            targetLinesToClear = 8,
            initialBoard = mapOf(
                HexCoord(0, -2) to CellData(value = -1, isLocked = true),
                HexCoord(2, -2) to CellData(value = -1, isLocked = true),
                HexCoord(2, 0) to CellData(value = -1, isLocked = true),
                HexCoord(0, 2) to CellData(value = -1, isLocked = true),
                HexCoord(-2, 2) to CellData(value = -1, isLocked = true),
                HexCoord(-2, 0) to CellData(value = -1, isLocked = true)
            )
        ),
        LevelTarget(
            levelNumber = 19,
            title = "Line Tempest",
            description = "Chain multiple line clears: clear 10 lines and score 76,000!",
            targetScore = 76000,
            targetEvolutionTier = 8,
            targetLinesToClear = 10
        ),
        LevelTarget(
            levelNumber = 20,
            title = "Tier 9 Royal Titan",
            description = "Synthesize a Tier 9 Royal titan with 90,000 points!",
            targetScore = 90000,
            targetEvolutionTier = 9,
            targetLinesToClear = 9
        ),

        // === EXPERT & MASTER STAGES (Levels 21 - 24) ===
        LevelTarget(
            levelNumber = 21,
            title = "Vortex of Obstacles",
            description = "5 crystal barriers in a spiral layout. Reach 105,000 score!",
            targetScore = 105000,
            targetEvolutionTier = 9,
            targetLinesToClear = 11,
            initialBoard = mapOf(
                HexCoord(-1, -1) to CellData(value = -1, isLocked = true),
                HexCoord(1, -2) to CellData(value = -1, isLocked = true),
                HexCoord(2, 0) to CellData(value = -1, isLocked = true),
                HexCoord(0, 2) to CellData(value = -1, isLocked = true),
                HexCoord(-2, 1) to CellData(value = -1, isLocked = true)
            )
        ),
        LevelTarget(
            levelNumber = 22,
            title = "Hexa Overdrive",
            description = "Clear 12 lines and synthesize a Tier 10 block!",
            targetScore = 120000,
            targetEvolutionTier = 10,
            targetLinesToClear = 12
        ),
        LevelTarget(
            levelNumber = 23,
            title = "Tier 11 Crimson",
            description = "Attain the Fiery Crimson Tier 11 block and clear 12 lines!",
            targetScore = 140000,
            targetEvolutionTier = 11,
            targetLinesToClear = 12
        ),
        LevelTarget(
            levelNumber = 24,
            title = "Grand Apex",
            description = "The ultimate challenge! Reach 165,000 score with Tier 12 and 15 lines!",
            targetScore = 165000,
            targetEvolutionTier = 12,
            targetLinesToClear = 15
        )
    )

    /**
     * Procedural infinite level generator for Level 25 and beyond.
     */
    fun generateProceduralLevel(levelNumber: Int): LevelTarget {
        val rand = Random(levelNumber.toLong() * 997L + 13L)
        val isBossStage = levelNumber % 5 == 0

        val targetScore = 165000 + (levelNumber - 24) * 8000
        val targetTier = (7 + ((levelNumber * 2) % 6)).coerceIn(7, 12)
        val targetLines = (10 + (levelNumber % 10)).coerceIn(8, 20)

        val stageTitles = listOf(
            "Cosmic Horizon", "Hyperion Core", "Astral Labyrinth",
            "Titan Citadel", "Quantum Weaver", "Eternity Crucible",
            "Prismatic Void", "Solaris Zenith", "Nebula Nexus"
        )
        val title = if (isBossStage) {
            "Boss Stage: Crystal Fortress $levelNumber"
        } else {
            "${stageTitles[rand.nextInt(stageTitles.size)]} $levelNumber"
        }

        val initialBoard = mutableMapOf<HexCoord, CellData>()
        if (isBossStage) {
            val radius = 2
            val obstaclesCount = 4 + (levelNumber % 4)
            val candidates = HexCoord.ALL_GRID_CELLS.filter { it.distanceToCenter() == radius }
            val chosen = candidates.shuffled(rand).take(obstaclesCount)
            for (coord in chosen) {
                initialBoard[coord] = CellData(value = -1, isLocked = true)
            }
        } else if (levelNumber % 3 == 0) {
            initialBoard[HexCoord(-1, 0)] = CellData(value = 3)
            initialBoard[HexCoord(1, 0)] = CellData(value = 3)
        }

        return LevelTarget(
            levelNumber = levelNumber,
            title = title,
            description = if (isBossStage) {
                "Boss Challenge! Navigate crystal barriers, reach Tier $targetTier, and score $targetScore!"
            } else {
                "Evolve to Tier $targetTier, clear $targetLines lines, and reach $targetScore points!"
            },
            targetScore = targetScore,
            targetEvolutionTier = targetTier,
            targetLinesToClear = targetLines,
            initialBoard = initialBoard
        )
    }

    fun calculateStars(levelTarget: LevelTarget, score: Int, highestTier: Int, linesCleared: Int): Int {
        var stars = 0
        val tierOk = levelTarget.targetEvolutionTier == 0 || highestTier >= levelTarget.targetEvolutionTier
        val linesOk = levelTarget.targetLinesToClear == 0 || linesCleared >= levelTarget.targetLinesToClear
        val scoreOk = score >= levelTarget.targetScore

        if (tierOk && linesOk && scoreOk) {
            stars = 1
            if (score >= (levelTarget.targetScore * 1.30).toInt()) {
                stars = 2
            }
            if (score >= (levelTarget.targetScore * 1.65).toInt()) {
                stars = 3
            }
        }
        return stars
    }
}
