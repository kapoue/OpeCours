package com.kapoue.opecours.data.remote.dto

import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.DateUtils

/**
 * Extensions pour mapper les réponses Finnhub vers les modèles domain
 */

/**
 * Convertit une réponse Finnhub Quote vers un objet Stock
 * @param operator L'opérateur télécom correspondant
 * @param historicalPrices Prix historiques (optionnel, récupérés séparément)
 * @return Objet Stock avec les données Finnhub
 */
fun FinnhubQuoteResponse.toStock(
    operator: Operator,
    historicalPrices: List<Double> = emptyList()
): Stock {
    return Stock(
        symbol = operator.symbol,
        operatorName = operator.displayName,
        currentPrice = currentPrice,
        openPrice = openPrice,
        previousClose = previousClose,
        change = change,
        changePercent = changePercent,
        lastUpdate = timestamp * 1000, // Convertir de secondes à millisecondes
        isMarketOpen = DateUtils.isMarketOpen(),
        historicalPrices = historicalPrices,
        volume = 0L // Volume non disponible dans l'endpoint quote, à récupérer séparément
    )
}

/**
 * Convertit une réponse Finnhub Candle vers une liste de prix de clôture
 * @return Liste des prix de clôture pour le graphique
 */
fun FinnhubCandleResponse.toHistoricalPrices(): List<Double> {
    return if (status == "ok" && closePrices != null) {
        closePrices.filterNotNull().takeLast(5) // Garder les 5 derniers jours
    } else {
        emptyList()
    }
}

/**
 * Convertit une réponse Finnhub Candle vers le volume le plus récent
 * @return Volume du dernier jour disponible
 */
fun FinnhubCandleResponse.toLatestVolume(): Long {
    return if (status == "ok" && volumes != null && volumes.isNotEmpty()) {
        volumes.filterNotNull().lastOrNull() ?: 0L
    } else {
        0L
    }
}

/**
 * Vérifie si la réponse Finnhub contient des données valides
 * @return true si les données sont valides
 */
fun FinnhubQuoteResponse.isValid(): Boolean {
    return currentPrice > 0 && 
           !currentPrice.isNaN() && 
           !currentPrice.isInfinite() &&
           timestamp > 0
}

/**
 * Vérifie si la réponse Finnhub Candle contient des données valides
 * @return true si les données sont valides
 */
fun FinnhubCandleResponse.isValid(): Boolean {
    return status == "ok" && 
           closePrices != null && 
           closePrices.isNotEmpty()
}