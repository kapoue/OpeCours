package com.kapoue.opecours.di

import android.content.Context
import androidx.room.Room
import com.kapoue.opecours.data.local.StockDao
import com.kapoue.opecours.data.local.StockDatabase
import com.kapoue.opecours.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideStockDatabase(
        @ApplicationContext context: Context
    ): StockDatabase {
        return Room.databaseBuilder(
            context,
            StockDatabase::class.java,
            Constants.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    @Singleton
    fun provideStockDao(database: StockDatabase): StockDao {
        return database.stockDao()
    }
}