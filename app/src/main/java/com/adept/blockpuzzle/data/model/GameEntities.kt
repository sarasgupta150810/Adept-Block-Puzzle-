package com.adept.blockpuzzle.data.model

data class GameRecordEntity(
    val id: String, // "classic" or "level_1", "level_2", etc.
    val highScore: Int = 0,
    val stars: Int = 0,
    val isUnlocked: Boolean = false,
    val highestTier: Int = 1,
    val updatedAt: Long = System.currentTimeMillis()
)

data class GameSettingEntity(
    val id: Int = 1,
    val musicEnabled: Boolean = true,
    val soundFxEnabled: Boolean = true,
    val brightness: Float = 0.85f
)

data class DailyChallengeEntity(
    val dateKey: String, // "yyyy-MM-dd"
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val stars: Int = 0,
    val highestTier: Int = 1,
    val streakCount: Int = 1,
    val completedAt: Long = System.currentTimeMillis()
)

data class UserEconomyEntity(
    val id: Int = 1,
    val coins: Int = 500, // starting gift coins
    val hammersCount: Int = 2,
    val shufflesCount: Int = 2,
    val rainbowCount: Int = 1,
    val ownedThemes: String = "cyber_neon", // comma-separated theme IDs
    val activeTheme: String = "cyber_neon",
    val lastDailyRewardClaimTime: Long = 0L,
    val lastAdRewardClaimTime: Long = 0L,
    val totalShapesCompleted: Int = 0,
    val currentShapeIndex: Int = 0,
    val currentShapeFilledBlocks: Int = 0
)
