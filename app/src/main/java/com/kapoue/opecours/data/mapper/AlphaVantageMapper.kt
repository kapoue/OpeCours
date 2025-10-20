package com.kapoue.opecours.data.mapper

import com.kapoue.opecours.data.remote.dto.AlphaVantageResponse
import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.DateUtils

fun AlphaVantageResponse.toStock(operator: Operator): Stock {
    val quote = globalQuote ?: throw Exception("Pas de données disponibles pour ${operator.displayName}")
    
    val currentPrice = quote.price.toDoubleOrNull() ?: 0.0
    val openPrice = quote.open.toDoubleOrNull() ?: currentPrice
    val previousClose = quote.previousClose.toDoubleOrNull() ?: currentPrice
    val change = quote.change.toDoubleOrNull() ?: 0.0
    val changePercentString = quote.changePercent.replace("%", "")
    val changePercent = changePercentString.toDoubleOrNull() ?: 0.0
    
    // Générer des données historiques simulées basées sur les données actuelles
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
        volume = quote.volume.toLongOrNull() ?: 0L
    )
}

private fun generateHistoricalPrices(previousClose: Double, currentPrice: Double): List<Double> {
    val prices = mutableListOf<Double>()
    val change = currentPrice - previousClose
    val dailyChange = change / 5 // Répartir le changement sur 5 jours
    
    for (i in 0..4) {
        val price = previousClose + (dailyChange * i)
        prices.add(price)
    }
    
    return prices
}