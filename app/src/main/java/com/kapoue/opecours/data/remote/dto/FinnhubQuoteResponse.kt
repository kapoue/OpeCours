package com.kapoue.opecours.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Réponse de l'API Finnhub pour le cours actuel d'une action
 * Documentation: https://finnhub.io/docs/api/quote
 */
data class FinnhubQuoteResponse(
    @SerializedName("c")
    val currentPrice: Double,        // Prix actuel (current)
    
    @SerializedName("d")
    val change: Double,              // Variation absolue en €
    
    @SerializedName("dp")
    val changePercent: Double,       // Variation en pourcentage
    
    @SerializedName("h")
    val highPrice: Double,           // Plus haut du jour
    
    @SerializedName("l")
    val lowPrice: Double,            // Plus bas du jour
    
    @SerializedName("o")
    val openPrice: Double,           // Prix d'ouverture
    
    @SerializedName("pc")
    val previousClose: Double,       // Clôture précédente
    
    @SerializedName("t")
    val timestamp: Long              // Timestamp Unix (secondes)
)