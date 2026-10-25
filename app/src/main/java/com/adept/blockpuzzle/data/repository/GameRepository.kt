package com.adept.blockpuzzle.data.repository

import com.adept.blockpuzzle.data.db.GameDao
import com.adept.blockpuzzle.data.model.DailyChallengeEntity
import com.adept.blockpuzzle.data.model.GameRecordEntity
import com.adept.blockpuzzle.data.model.GameSettingEntity
import com.adept.blockpuzzle.data.model.UserEconomyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GameRepository(private val gameDao: GameDao) {

    val settings: Flow<GameSettingEntity> = gameDao.getSettings().map {
        it ?: GameSettingEntity()
    }

    val economy: Flow<UserEconomyEntity> = gameDao.getEconomy().map {
        it ?: UserEconomyEntity()
    }

    val classicRecord: Flow<GameRecordEntity?> = gameDao.getRecord("classic")

    val allRecords: Flow<List<GameRecordEntity>> = gameDao.getAllRecords()

    suspend fun saveClassicHighScore(score: Int, highestTier: Int) {
        gameDao.insertOrUpdateRecord(
            GameRecordEntity(
                id = "classic",
                highScore = score,
                isUnlocked = true,
                highestTier = highestTier
            )
        )
    }

    suspend fun saveLevelResult(levelId: Int, score: Int, stars: Int, highestTier: Int) {
        val recordId = "level_$levelId"
        gameDao.insertOrUpdateRecord(
            GameRecordEntity(
                id = recordId,
                highScore = score,
                stars = stars,
                isUnlocked = true,
                highestTier = highestTier
            )
        )
        // Unlock next level (supports infinite levels $levelId + 1)
        val nextLevelId = "level_${levelId + 1}"
        gameDao.insertOrUpdateRecord(
            GameRecordEntity(
                id = nextLevelId,
                highScore = 0,
                stars = 0,
                isUnlocked = true,
                highestTier = 1
            )
        )
    }

    suspend fun updateSettings(music: Boolean, soundFx: Boolean, brightness: Float) {
        gameDao.saveSettings(
            GameSettingEntity(
                id = 1,
                musicEnabled = music,
                soundFxEnabled = soundFx,
                brightness = brightness
            )
        )
    }

    // Daily Challenge
    fun getDailyRecord(dateKey: String): Flow<DailyChallengeEntity?> = gameDao.getDailyRecord(dateKey)

    fun getLatestDailyRecord(): Flow<DailyChallengeEntity?> = gameDao.getLatestDailyRecord()

    fun getAllCompletedDailyRecords(): Flow<List<DailyChallengeEntity>> = gameDao.getAllCompletedDailyRecords()

    suspend fun saveDailyResult(dateKey: String, score: Int, highestTier: Int, streak: Int) {
        gameDao.insertOrUpdateDailyRecord(
            DailyChallengeEntity(
                dateKey = dateKey,
                isCompleted = true,
                score = score,
                stars = 3,
                highestTier = highestTier,
                streakCount = streak,
                completedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun saveEconomy(economy: UserEconomyEntity) {
        gameDao.saveEconomy(economy)
    }

    suspend fun addCoins(amount: Int) {
        val current = economy.first()
        gameDao.saveEconomy(current.copy(coins = current.coins + amount))
    }

    suspend fun spendCoins(amount: Int): Boolean {
        val current = economy.first()
        if (current.coins >= amount) {
            gameDao.saveEconomy(current.copy(coins = current.coins - amount))
            return true
        }
        return false
    }
}
