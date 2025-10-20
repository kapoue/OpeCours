package com.kapoue.opecours.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.kapoue.opecours.data.local.entities.StockEntity

@Database(
    entities = [StockEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
}