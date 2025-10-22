package com.kapoue.opecours.util

object Constants {
    // 🔄 Migration vers Finnhub API pour données 15 minutes
    const val FINNHUB_BASE_URL = "https://finnhub.io/api/v1/"
    
    // 🔑 Clé API Finnhub - Configurée pour kapoue@gmail.com
    // Plan gratuit: 60 appels/minute, délai 15 minutes
    const val FINNHUB_API_KEY = "d3se7o9r01qvii72sk0gd3se7o9r01qvii72sk10"
    
    // 📊 Ancienne configuration Alpha Vantage (conservée en fallback)
    const val ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/"
    const val ALPHA_VANTAGE_API_KEY = "AB8FB9V1ZDFC6KDS"
    
    const val DATABASE_NAME = "opecours_db"
    const val AUTO_REFRESH_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
    
    // Heures de marché (Paris/Euronext)
    const val MARKET_OPEN_HOUR = 9
    const val MARKET_OPEN_MINUTE = 0
    const val MARKET_CLOSE_HOUR = 17
    const val MARKET_CLOSE_MINUTE = 30
    
    // Configuration pour désactiver le fallback vers les données mock
    const val USE_MOCK_DATA_FALLBACK = false // Mettre à true seulement pour les tests
    
    // 🎯 Configuration Finnhub
    const val USE_FINNHUB_API = true // true = Finnhub, false = Alpha Vantage
}