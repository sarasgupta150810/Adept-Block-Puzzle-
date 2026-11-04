package com.adept.blockpuzzle.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.adept.blockpuzzle.data.model.DailyChallengeEntity
import com.adept.blockpuzzle.data.model.GameRecordEntity
import com.adept.blockpuzzle.data.model.GameSettingEntity
import com.adept.blockpuzzle.data.model.UserEconomyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Clean SQLite-backed AppDatabase and GameDao implementation without annotation processors
 * to eliminate KSP/Room compiler crashes in mobile and streaming Android build environments.
 */
class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    "block_evolution.db",
    null,
    DATABASE_VERSION
), GameDao {

    companion object {
        private const val DATABASE_VERSION = 4

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabase(context)
                INSTANCE = instance
                instance.initFlows()
                instance
            }
        }
    }

    // Reactive StateFlows for all queries
    private val recordsFlow = MutableStateFlow<Map<String, GameRecordEntity>>(emptyMap())
    private val settingsFlow = MutableStateFlow<GameSettingEntity?>(null)
    private val dailyRecordsFlow = MutableStateFlow<Map<String, DailyChallengeEntity>>(emptyMap())
    private val economyFlow = MutableStateFlow<UserEconomyEntity?>(null)

    fun gameDao(): GameDao = this

    private fun initFlows() {
        val db = readableDatabase
        // Load Game records
        val loadedRecords = mutableMapOf<String, GameRecordEntity>()
        db.rawQuery("SELECT id, highScore, stars, isUnlocked, highestTier, updatedAt FROM game_records", null).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                loadedRecords[id] = GameRecordEntity(
                    id = id,
                    highScore = cursor.getInt(1),
                    stars = cursor.getInt(2),
                    isUnlocked = cursor.getInt(3) == 1,
                    highestTier = cursor.getInt(4),
                    updatedAt = cursor.getLong(5)
                )
            }
        }
        recordsFlow.value = loadedRecords

        // Load Game Settings
        db.rawQuery("SELECT id, musicEnabled, soundFxEnabled, brightness FROM game_settings WHERE id = 1", null).use { cursor ->
            if (cursor.moveToFirst()) {
                settingsFlow.value = GameSettingEntity(
                    id = cursor.getInt(0),
                    musicEnabled = cursor.getInt(1) == 1,
                    soundFxEnabled = cursor.getInt(2) == 1,
                    brightness = cursor.getFloat(3)
                )
            } else {
                settingsFlow.value = GameSettingEntity()
            }
        }

        // Load Daily records
        val loadedDaily = mutableMapOf<String, DailyChallengeEntity>()
        db.rawQuery("SELECT dateKey, isCompleted, score, stars, highestTier, streakCount, completedAt FROM daily_records", null).use { cursor ->
            while (cursor.moveToNext()) {
                val key = cursor.getString(0)
                loadedDaily[key] = DailyChallengeEntity(
                    dateKey = key,
                    isCompleted = cursor.getInt(1) == 1,
                    score = cursor.getInt(2),
                    stars = cursor.getInt(3),
                    highestTier = cursor.getInt(4),
                    streakCount = cursor.getInt(5),
                    completedAt = cursor.getLong(6)
                )
            }
        }
        dailyRecordsFlow.value = loadedDaily

        // Load Economy
        db.rawQuery(
            "SELECT id, coins, hammersCount, shufflesCount, rainbowCount, ownedThemes, activeTheme, lastDailyRewardClaimTime, lastAdRewardClaimTime, totalShapesCompleted, currentShapeIndex, currentShapeFilledBlocks FROM user_economy WHERE id = 1",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                economyFlow.value = UserEconomyEntity(
                    id = cursor.getInt(0),
                    coins = cursor.getInt(1),
                    hammersCount = cursor.getInt(2),
                    shufflesCount = cursor.getInt(3),
                    rainbowCount = cursor.getInt(4),
                    ownedThemes = cursor.getString(5),
                    activeTheme = cursor.getString(6),
                    lastDailyRewardClaimTime = cursor.getLong(7),
                    lastAdRewardClaimTime = cursor.getLong(8),
                    totalShapesCompleted = cursor.getInt(9),
                    currentShapeIndex = cursor.getInt(10),
                    currentShapeFilledBlocks = cursor.getInt(11)
                )
            } else {
                economyFlow.value = UserEconomyEntity()
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS game_records (
                id TEXT PRIMARY KEY NOT NULL,
                highScore INTEGER NOT NULL,
                stars INTEGER NOT NULL,
                isUnlocked INTEGER NOT NULL,
                highestTier INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS game_settings (
                id INTEGER PRIMARY KEY NOT NULL,
                musicEnabled INTEGER NOT NULL,
                soundFxEnabled INTEGER NOT NULL,
                brightness REAL NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS daily_records (
                dateKey TEXT PRIMARY KEY NOT NULL,
                isCompleted INTEGER NOT NULL,
                score INTEGER NOT NULL,
                stars INTEGER NOT NULL,
                highestTier INTEGER NOT NULL,
                streakCount INTEGER NOT NULL,
                completedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS user_economy (
                id INTEGER PRIMARY KEY NOT NULL,
                coins INTEGER NOT NULL,
                hammersCount INTEGER NOT NULL,
                shufflesCount INTEGER NOT NULL,
                rainbowCount INTEGER NOT NULL,
                ownedThemes TEXT NOT NULL,
                activeTheme TEXT NOT NULL,
                lastDailyRewardClaimTime INTEGER NOT NULL,
                lastAdRewardClaimTime INTEGER NOT NULL,
                totalShapesCompleted INTEGER NOT NULL,
                currentShapeIndex INTEGER NOT NULL,
                currentShapeFilledBlocks INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS game_records")
        db.execSQL("DROP TABLE IF EXISTS game_settings")
        db.execSQL("DROP TABLE IF EXISTS daily_records")
        db.execSQL("DROP TABLE IF EXISTS user_economy")
        onCreate(db)
    }

    // --- GameDao Methods ---

    override fun getRecord(recordId: String): Flow<GameRecordEntity?> {
        return recordsFlow.map { map -> map[recordId] }
    }

    override fun getAllRecords(): Flow<List<GameRecordEntity>> {
        return recordsFlow.map { map -> map.values.toList() }
    }

    override suspend fun insertOrUpdateRecord(record: GameRecordEntity) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", record.id)
            put("highScore", record.highScore)
            put("stars", record.stars)
            put("isUnlocked", if (record.isUnlocked) 1 else 0)
            put("highestTier", record.highestTier)
            put("updatedAt", record.updatedAt)
        }
        db.insertWithOnConflict("game_records", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        recordsFlow.value = recordsFlow.value + (record.id to record)
    }

    override fun getSettings(): Flow<GameSettingEntity?> = settingsFlow.asStateFlow()

    override suspend fun saveSettings(setting: GameSettingEntity) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", setting.id)
            put("musicEnabled", if (setting.musicEnabled) 1 else 0)
            put("soundFxEnabled", if (setting.soundFxEnabled) 1 else 0)
            put("brightness", setting.brightness)
        }
        db.insertWithOnConflict("game_settings", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        settingsFlow.value = setting
    }

    override fun getDailyRecord(dateKey: String): Flow<DailyChallengeEntity?> {
        return dailyRecordsFlow.map { map -> map[dateKey] }
    }

    override fun getLatestDailyRecord(): Flow<DailyChallengeEntity?> {
        return dailyRecordsFlow.map { map ->
            map.values.maxByOrNull { it.completedAt }
        }
    }

    override fun getAllCompletedDailyRecords(): Flow<List<DailyChallengeEntity>> {
        return dailyRecordsFlow.map { map ->
            map.values.filter { it.isCompleted }
        }
    }

    override suspend fun insertOrUpdateDailyRecord(record: DailyChallengeEntity) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("dateKey", record.dateKey)
            put("isCompleted", if (record.isCompleted) 1 else 0)
            put("score", record.score)
            put("stars", record.stars)
            put("highestTier", record.highestTier)
            put("streakCount", record.streakCount)
            put("completedAt", record.completedAt)
        }
        db.insertWithOnConflict("daily_records", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        dailyRecordsFlow.value = dailyRecordsFlow.value + (record.dateKey to record)
    }

    override fun getEconomy(): Flow<UserEconomyEntity?> = economyFlow.asStateFlow()

    override suspend fun saveEconomy(economy: UserEconomyEntity) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", economy.id)
            put("coins", economy.coins)
            put("hammersCount", economy.hammersCount)
            put("shufflesCount", economy.shufflesCount)
            put("rainbowCount", economy.rainbowCount)
            put("ownedThemes", economy.ownedThemes)
            put("activeTheme", economy.activeTheme)
            put("lastDailyRewardClaimTime", economy.lastDailyRewardClaimTime)
            put("lastAdRewardClaimTime", economy.lastAdRewardClaimTime)
            put("totalShapesCompleted", economy.totalShapesCompleted)
            put("currentShapeIndex", economy.currentShapeIndex)
            put("currentShapeFilledBlocks", economy.currentShapeFilledBlocks)
        }
        db.insertWithOnConflict("user_economy", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        economyFlow.value = economy
    }
}
