package com.adept.blockpuzzle.data.db

import com.adept.blockpuzzle.data.model.DailyChallengeEntity
import com.adept.blockpuzzle.data.model.GameRecordEntity
import com.adept.blockpuzzle.data.model.GameSettingEntity
import com.adept.blockpuzzle.data.model.UserEconomyEntity
import kotlinx.coroutines.flow.Flow

interface GameDao {
    fun getRecord(recordId: String): Flow<GameRecordEntity?>

    fun getAllRecords(): Flow<List<GameRecordEntity>>

    suspend fun insertOrUpdateRecord(record: GameRecordEntity)

    fun getSettings(): Flow<GameSettingEntity?>

    suspend fun saveSettings(setting: GameSettingEntity)

    // Daily Challenge queries
    fun getDailyRecord(dateKey: String): Flow<DailyChallengeEntity?>

    fun getLatestDailyRecord(): Flow<DailyChallengeEntity?>

    fun getAllCompletedDailyRecords(): Flow<List<DailyChallengeEntity>>

    suspend fun insertOrUpdateDailyRecord(record: DailyChallengeEntity)

    // User Economy & Store queries
    fun getEconomy(): Flow<UserEconomyEntity?>

    suspend fun saveEconomy(economy: UserEconomyEntity)
}
