package com.kapoue.opecours.data.mock

import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.DateUtils

object MockStockData {
    
    // Prix de base (valeurs réelles approximatives)
    private val basePrices = mapOf(
        "ORA.PA" to 14.16,
        "EN.PA" to 41.16,
        "ILD.PA" to 182.00
    )
    
    fun getMockStocks(): List<Stock> {
        // Générer des variations réalistes basées sur l'heure actuelle
        val currentTime = System.currentTimeMillis()
        val seed = (currentTime / (1000 * 60 * 15)).toInt() // Change toutes les 15 minutes
        
        return listOf(
            createRealisticMockStock(Operator.ORANGE, "ORA.PA", seed),
            createRealisticMockStock(Operator.BOUYGUES, "EN.PA", seed + 1),
            createRealisticMockStock(Operator.FREE, "ILD.PA", seed + 2)
        )
    }
    
    private fun createRealisticMockStock(operator: Operator, symbol: String, seed: Int): Stock {
        val basePrice = basePrices[symbol] ?: 10.0
        
        // Générer une variation réaliste (-2% à +2%)
        val random = kotlin.random.Random(seed)
        val variation = (random.nextDouble() - 0.5) * 0.04 // -2% à +2%
        val currentPrice = basePrice * (1 + variation)
        
        // Prix de clôture précédent (légèrement différent)
        val previousVariation = (random.nextDouble() - 0.5) * 0.02
        val previousClose = basePrice * (1 + previousVariation)
        
        return createMockStock(
            operator = operator,
            currentPrice = currentPrice,
            previousClose = previousClose,
            historicalPrices = generateRealisticHistory(basePrice, currentPrice, random)
        )
    }
    
    private fun generateRealisticHistory(basePrice: Double, currentPrice: Double, random: kotlin.random.Random): List<Double> {
        val history = mutableListOf<Double>()
        var price = basePrice * 0.98 // Commencer légèrement en dessous
        
        for (i in 0..4) {
            val dailyVariation = (random.nextDouble() - 0.5) * 0.03 // -1.5% à +1.5% par jour
            price *= (1 + dailyVariation)
            history.add(price)
        }
        
        // S'assurer que le dernier prix est proche du prix actuel
        history[4] = currentPrice
        
        return history
    }
    
    private fun createMockStock(
        operator: Operator,
        currentPrice: Double,
        previousClose: Double,
        historicalPrices: List<Double>
    ): Stock {
        val change = currentPrice - previousClose
        val changePercent = (change / previousClose) * 100
        
        return Stock(
            symbol = operator.symbol,
            operatorName = operator.displayName,
            currentPrice = currentPrice,
            openPrice = previousClose + (change * 0.3), // Simulation d'ouverture
            previousClose = previousClose,
            change = change,
            changePercent = changePercent,
            lastUpdate = System.currentTimeMillis(),
            isMarketOpen = DateUtils.isMarketOpen(),
            historicalPrices = historicalPrices,
            volume = (1000000..5000000).random().toLong() // Volume aléatoire
        )
    }
}