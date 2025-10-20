package com.kapoue.opecours.data.remote.dto

import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.DateUtils

object AlphaVantageMapper {
    
    fun toStock(response: AlphaVantageResponse, operator: Operator): Stock {
        val quote = response.globalQuote ?: throw Exception("Pas de données GlobalQuote disponibles")
        
        val currentPrice = quote.price.toDoubleOrNull() ?: 0.0
        val openPrice = quote.open.toDoubleOrNull() ?: currentPrice
        val previousClose = quote.previousClose.toDoubleOrNull() ?: currentPrice
        val change = quote.change.toDoubleOrNull() ?: 0.0
        val changePercent = quote.changePercent.replace("%", "").toDoubleOrNull() ?: 0.0
        val volume = quote.volume.toLongOrNull() ?: 0L
        
        // Générer des données historiques simulées pour le graphique
        val historicalPrices = generateHistoricalPrices(previousClose, currentPrice)
        
        return Stock(
            symbol = operator.symbol,
            operatorName = operator.displayName,
            currentPrice = currentPrice,
            openPrice = openPrice,
            previousClose = previousClose,
            change = change,
            changePercent = changePercent,
            lastUpdate = System.currentTimeMillis(),
            isMarketOpen = DateUtils.isMarketOpen(),
            historicalPrices = historicalPrices,
            volume = volume
        )
    }
    
    private fun generateHistoricalPrices(start: Double, end: Double): List<Double> {
        val prices = mutableListOf<Double>()
        val step = (end - start) / 4
        for (i in 0..4) {
            prices.add(start + (step * i) + (Math.random() - 0.5) * 0.5) // Ajouter un peu de variation
        }
        return prices
    }
}