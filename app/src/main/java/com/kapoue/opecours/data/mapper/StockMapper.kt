package com.kapoue.opecours.data.mapper

import com.kapoue.opecours.data.local.entities.StockEntity
import com.kapoue.opecours.data.remote.dto.StockResponse
import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.DateUtils

fun StockResponse.toStock(operator: Operator): Stock {
    // Vérifier que la réponse contient des données
    val resultList = chart.result
    if (resultList.isNullOrEmpty()) {
        throw Exception("Pas de données disponibles pour ${operator.displayName}")
    }
    
    val result = resultList.first()
    val meta = result.meta
    val quoteList = result.indicators.quote
    
    if (quoteList.isNullOrEmpty()) {
        throw Exception("Pas de cotations pour ${operator.displayName}")
    }
    
    val quote = quoteList.first()
    
    val currentPrice = meta.regularMarketPrice
    val openPrice = meta.regularMarketOpen ?: currentPrice
    val previousClose = meta.previousClose
    val change = currentPrice - previousClose
    val changePercent = if (previousClose != 0.0) (change / previousClose) * 100 else 0.0
    
    // Extraire les 5 derniers prix de clôture pour le graphique
    val historicalPrices = quote.close
        ?.filterNotNull()
        ?.takeLast(5)
        ?: emptyList()
    
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
        volume = meta.regularMarketVolume ?: 0L
    )
}

fun Stock.toEntity(): StockEntity {
    // Conversion simple en string séparée par des virgules
    val historicalJson = historicalPrices.joinToString(",")
    
    return StockEntity(
        symbol = symbol,
        operatorName = operatorName,
        currentPrice = currentPrice,
        openPrice = openPrice,
        previousClose = previousClose,
        change = change,
        changePercent = changePercent,
        lastUpdate = lastUpdate,
        isMarketOpen = isMarketOpen,
        historicalPrices = historicalJson,
        volume = volume
    )
}

fun StockEntity.toDomain(): Stock {
    // Conversion depuis string séparée par des virgules
    val historicalList = try {
        if (historicalPrices.isBlank()) {
            emptyList()
        } else {
            historicalPrices.split(",").mapNotNull { it.toDoubleOrNull() }
        }
    } catch (e: Exception) {
        emptyList()
    }
    
    return Stock(
        symbol = symbol,
        operatorName = operatorName,
        currentPrice = currentPrice,
        openPrice = openPrice,
        previousClose = previousClose,
        change = change,
        changePercent = changePercent,
        lastUpdate = lastUpdate,
        isMarketOpen = isMarketOpen,
        historicalPrices = historicalList,
        volume = volume
    )
}