package com.adept.blockpuzzle.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adept.blockpuzzle.data.model.DailyChallengeEntity
import com.adept.blockpuzzle.data.model.GameRecordEntity
import com.adept.blockpuzzle.data.model.GameSettingEntity
import com.adept.blockpuzzle.data.model.UserEconomyEntity

@Database(
    entities = [GameRecordEntity::class, GameSettingEntity::class, DailyChallengeEntity::class, UserEconomyEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "block_evolution.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
