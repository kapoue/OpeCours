package com.kapoue.opecours.util

object Constants {
    const val BASE_URL = "https://www.alphavantage.co/"
    const val API_KEY = "demo" // Clé démo gratuite - remplacer par une vraie clé si nécessaire
    const val DATABASE_NAME = "opecours_db"
    const val AUTO_REFRESH_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
    
    // Heures de marché (Paris/Euronext)
    const val MARKET_OPEN_HOUR = 9
    const val MARKET_OPEN_MINUTE = 0
    const val MARKET_CLOSE_HOUR = 17
    const val MARKET_CLOSE_MINUTE = 30
}