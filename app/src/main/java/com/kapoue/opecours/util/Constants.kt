package com.kapoue.opecours.util

object Constants {
    const val BASE_URL = "https://www.alphavantage.co/"
    
    // ⚠️ IMPORTANT: Remplacez "demo" par votre vraie clé Alpha Vantage
    // Pour obtenir une clé gratuite: https://www.alphavantage.co/support/#api-key
    // La clé "demo" a des limitations sévères et ne fonctionne que pour certains symboles
    const val API_KEY = "demo" // TODO: Remplacer par votre vraie clé Alpha Vantage
    
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