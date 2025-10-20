package com.kapoue.opecours.domain.model

data class Stock(
    val symbol: String,              // Ex: "ORA.PA"
    val operatorName: String,        // Ex: "Orange"
    val currentPrice: Double,        // Prix actuel
    val openPrice: Double,           // Prix à l'ouverture
    val previousClose: Double,       // Clôture veille
    val change: Double,              // Variation en €
    val changePercent: Double,       // Variation en %
    val lastUpdate: Long,            // Timestamp
    val isMarketOpen: Boolean,       // Marché ouvert/fermé
    val historicalPrices: List<Double>, // 5 derniers jours pour graphique
    val volume: Long                 // Volume d'échanges
)