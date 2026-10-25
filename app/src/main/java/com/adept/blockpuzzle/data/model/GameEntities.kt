package com.adept.blockpuzzle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey val id: String, // "classic" or "level_1", "level_2", etc.
    val highScore: Int = 0,
    val stars: Int = 0,
    val isUnlocked: Boolean = false,
    val highestTier: Int = 1,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_settings")
data class GameSettingEntity(
    @PrimaryKey val id: Int = 1,
    val musicEnabled: Boolean = true,
    val soundFxEnabled: Boolean = true,
    val brightness: Float = 0.85f
)

@Entity(tableName = "daily_records")
data class DailyChallengeEntity(
    @PrimaryKey val dateKey: String, // "yyyy-MM-dd"
    val isCompleted: Boolean = false,
    val score: Int = 0,
    val stars: Int = 0,
    val highestTier: Int = 1,
    val streakCount: Int = 1,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_economy")
data class UserEconomyEntity(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 500, // starting gift coins
    val hammersCount: Int = 2,
    val shufflesCount: Int = 2,
    val rainbowCount: Int = 1,
    val ownedThemes: String = "cyber_neon", // comma-separated theme IDs
    val activeTheme: String = "cyber_neon",
    val lastDailyRewardClaimTime: Long = 0L,
    val lastAdRewardClaimTime: Long = 0L
)
