package com.kapoue.opecours.util

object Constants {
    const val BASE_URL = "https://www.alphavantage.co/"
    
    // ✅ Clé Alpha Vantage configurée pour kapoue@gmail.com
    // Clé gratuite avec 500 requêtes/jour et support des symboles européens
    const val API_KEY = "AB8FB9V1ZDFC6KDS"
    
    const val DATABASE_NAME = "opecours_db"
    const val AUTO_REFRESH_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
    
    // Heures de marché (Paris/Euronext)
    const val MARKET_OPEN_HOUR = 9
    const val MARKET_OPEN_MINUTE = 0
    const val MARKET_CLOSE_HOUR = 17
    const val MARKET_CLOSE_MINUTE = 30
    
    // Configuration pour désactiver le fallback vers les données mock
    const val USE_MOCK_DATA_FALLBACK = false // Mettre à true seulement pour les tests
}