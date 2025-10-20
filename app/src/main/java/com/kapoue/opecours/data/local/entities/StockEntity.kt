package com.kapoue.opecours.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey val symbol: String,
    val operatorName: String,
    val currentPrice: Double,
    val openPrice: Double,
    val previousClose: Double,
    val change: Double,
    val changePercent: Double,
    val lastUpdate: Long,
    val isMarketOpen: Boolean,
    val historicalPrices: String, // JSON string
    val volume: Long
)