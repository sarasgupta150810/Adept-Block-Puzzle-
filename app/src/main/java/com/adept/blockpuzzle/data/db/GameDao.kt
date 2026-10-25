package com.adept.blockpuzzle.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adept.blockpuzzle.data.model.DailyChallengeEntity
import com.adept.blockpuzzle.data.model.GameRecordEntity
import com.adept.blockpuzzle.data.model.GameSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_records WHERE id = :recordId")
    fun getRecord(recordId: String): Flow<GameRecordEntity?>

    @Query("SELECT * FROM game_records")
    fun getAllRecords(): Flow<List<GameRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(record: GameRecordEntity)

    @Query("SELECT * FROM game_settings WHERE id = 1")
    fun getSettings(): Flow<GameSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(setting: GameSettingEntity)

    // Daily Challenge queries
    @Query("SELECT * FROM daily_records WHERE dateKey = :dateKey")
    fun getDailyRecord(dateKey: String): Flow<DailyChallengeEntity?>

    @Query("SELECT * FROM daily_records ORDER BY completedAt DESC LIMIT 1")
    fun getLatestDailyRecord(): Flow<DailyChallengeEntity?>

    @Query("SELECT * FROM daily_records WHERE isCompleted = 1")
    fun getAllCompletedDailyRecords(): Flow<List<DailyChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyRecord(record: DailyChallengeEntity)

    // User Economy & Store queries
    @Query("SELECT * FROM user_economy WHERE id = 1")
    fun getEconomy(): Flow<com.adept.blockpuzzle.data.model.UserEconomyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEconomy(economy: com.adept.blockpuzzle.data.model.UserEconomyEntity)
}
