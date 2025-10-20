package com.kapoue.opecours.data.mock

import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.DateUtils

object MockStockData {
    
    fun getMockStocks(): List<Stock> {
        return listOf(
            createMockStock(
                operator = Operator.ORANGE,
                currentPrice = 14.16,
                previousClose = 14.05,
                historicalPrices = listOf(13.95, 14.02, 14.08, 14.12, 14.16)
            ),
            createMockStock(
                operator = Operator.BOUYGUES,
                currentPrice = 41.16,
                previousClose = 40.85,
                historicalPrices = listOf(40.50, 40.70, 40.90, 41.05, 41.16)
            ),
            createMockStock(
                operator = Operator.FREE,
                currentPrice = 182.00,
                previousClose = 180.50,
                historicalPrices = listOf(179.00, 180.00, 181.00, 181.50, 182.00)
            )
            // SFR retiré car pas de données disponibles
        )
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