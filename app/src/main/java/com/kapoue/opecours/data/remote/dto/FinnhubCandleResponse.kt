package com.kapoue.opecours.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Réponse de l'API Finnhub pour les données historiques (chandelles)
 * Documentation: https://finnhub.io/docs/api/stock-candles
 */
data class FinnhubCandleResponse(
    @SerializedName("c")
    val closePrices: List<Double>?,     // Prix de clôture
    
    @SerializedName("h")
    val highPrices: List<Double>?,      // Prix les plus hauts
    
    @SerializedName("l")
    val lowPrices: List<Double>?,       // Prix les plus bas
    
    @SerializedName("o")
    val openPrices: List<Double>?,      // Prix d'ouverture
    
    @SerializedName("t")
    val timestamps: List<Long>?,        // Timestamps Unix (secondes)
    
    @SerializedName("v")
    val volumes: List<Long>?,           // Volumes d'échanges
    
    @SerializedName("s")
    val status: String                  // Statut de la réponse ("ok" ou "no_data")
)