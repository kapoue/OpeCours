package com.kapoue.opecours.data.repository

import com.kapoue.opecours.data.local.StockDao
import com.kapoue.opecours.data.mapper.toDomain
import com.kapoue.opecours.data.mapper.toEntity
import com.kapoue.opecours.data.remote.dto.AlphaVantageMapper
import com.kapoue.opecours.data.remote.dto.toStock
import com.kapoue.opecours.data.remote.dto.toHistoricalPrices
import com.kapoue.opecours.data.remote.dto.isValid
import com.kapoue.opecours.data.mock.MockStockData
import com.kapoue.opecours.data.remote.AlphaVantageApi
import com.kapoue.opecours.data.remote.FinnhubApi
import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.Constants
import com.kapoue.opecours.util.NetworkUtils
import com.kapoue.opecours.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val finnhubApi: FinnhubApi,
    private val alphaVantageApi: AlphaVantageApi,
    private val dao: StockDao,
    private val networkUtils: NetworkUtils
) {
    fun getStocks(): Flow<Resource<List<Stock>>> = flow {
        emit(Resource.Loading())
        
        // Émettre les données en cache d'abord
        try {
            val cachedStocks = dao.getAllStocks().first().map { it.toDomain() }
            if (cachedStocks.isNotEmpty()) {
                emit(Resource.Success(cachedStocks))
            }
        } catch (e: Exception) {
            // Ignore cache errors
        }
        
        // Puis tenter de rafraîchir depuis l'API
        if (networkUtils.isNetworkAvailable()) {
            try {
                val stocks = fetchFromApi()
                dao.insertAll(stocks.map { it.toEntity() })
                emit(Resource.Success(stocks))
            } catch (e: HttpException) {
                emit(Resource.Error("Erreur serveur (${e.code()}): ${e.message()}"))
            } catch (e: IOException) {
                emit(Resource.Error("Erreur de connexion Internet"))
            } catch (e: Exception) {
                emit(Resource.Error("Erreur lors de la récupération des données: ${e.message}"))
            }
        } else {
            try {
                val cachedStocks = dao.getAllStocks().first().map { it.toDomain() }
                if (cachedStocks.isEmpty()) {
                    emit(Resource.Error("Pas de connexion Internet et aucune donnée en cache"))
                }
            } catch (e: Exception) {
                emit(Resource.Error("Pas de connexion Internet"))
            }
        }
    }
    
    private suspend fun fetchFromApi(): List<Stock> {
        // Essayer Finnhub d'abord
        return try {
            val activeOperators = Operator.values().filter { it.isActive }
            activeOperators.map { operator ->
                fetchStockFromFinnhub(operator)
            }
        } catch (e: Exception) {
            // Fallback vers Alpha Vantage
            try {
                val activeOperators = Operator.values().filter { it.isActive }
                activeOperators.map { operator ->
                    val response = alphaVantageApi.getQuote(
                        symbol = operator.symbol,
                        apiKey = Constants.ALPHA_VANTAGE_API_KEY
                    )
                    
                    if (response.isSuccessful) {
                        val body = response.body()
                        val stockData = body?.let { AlphaVantageMapper.toStock(it, operator) }
                            ?: throw Exception("Données invalides pour ${operator.displayName}")
                        stockData
                    } else {
                        throw HttpException(response)
                    }
                }
            } catch (fallbackException: Exception) {
                if (Constants.USE_MOCK_DATA_FALLBACK) {
                    val mockStocks = MockStockData.getMockStocks().filter { stock ->
                        Operator.values().find { it.symbol == stock.symbol }?.isActive == true
                    }
                    mockStocks
                } else {
                    throw fallbackException
                }
            }
        }
    }
    
    /**
     * Récupère les données d'un stock depuis Finnhub
     */
    private suspend fun fetchStockFromFinnhub(operator: Operator): Stock {
        // 1. Récupérer le cours actuel
        val quoteResponse = finnhubApi.getQuote(
            symbol = operator.symbol,
            token = Constants.FINNHUB_API_KEY
        )
        
        if (!quoteResponse.isSuccessful) {
            throw HttpException(quoteResponse)
        }
        
        val quoteData = quoteResponse.body()
        if (quoteData == null || !quoteData.isValid()) {
            throw Exception("Données invalides pour ${operator.displayName}")
        }
        
        // 2. Récupérer les données historiques pour le graphique
        val historicalPrices = try {
            val endTime = System.currentTimeMillis() / 1000
            val startTime = endTime - (7 * 24 * 60 * 60)
            
            val candleResponse = finnhubApi.getCandles(
                symbol = operator.symbol,
                resolution = "D",
                from = startTime,
                to = endTime,
                token = Constants.FINNHUB_API_KEY
            )
            
            if (candleResponse.isSuccessful) {
                val candleData = candleResponse.body()
                if (candleData != null && candleData.isValid()) {
                    candleData.toHistoricalPrices()
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
        
        // 3. Convertir vers notre modèle Stock
        return quoteData.toStock(operator, historicalPrices)
    }
    
    suspend fun refreshStocks(): Resource<List<Stock>> {
        return try {
            if (!networkUtils.isNetworkAvailable()) {
                if (Constants.USE_MOCK_DATA_FALLBACK) {
                    val mockStocks = MockStockData.getMockStocks()
                    dao.insertAll(mockStocks.map { it.toEntity() })
                    return Resource.Success(mockStocks)
                } else {
                    return Resource.Error("Pas de connexion Internet")
                }
            }
            
            val stocks = fetchFromApi()
            dao.insertAll(stocks.map { it.toEntity() })
            Resource.Success(stocks)
        } catch (e: HttpException) {
            val errorMsg = "Erreur serveur (${e.code()}): ${e.message()}"
            if (Constants.USE_MOCK_DATA_FALLBACK) {
                val mockStocks = MockStockData.getMockStocks()
                dao.insertAll(mockStocks.map { it.toEntity() })
                Resource.Success(mockStocks)
            } else {
                Resource.Error(errorMsg)
            }
        } catch (e: IOException) {
            val errorMsg = "Erreur de connexion Internet"
            if (Constants.USE_MOCK_DATA_FALLBACK) {
                val mockStocks = MockStockData.getMockStocks()
                dao.insertAll(mockStocks.map { it.toEntity() })
                Resource.Success(mockStocks)
            } else {
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Erreur lors de la récupération des données: ${e.message}"
            if (Constants.USE_MOCK_DATA_FALLBACK) {
                val mockStocks = MockStockData.getMockStocks()
                dao.insertAll(mockStocks.map { it.toEntity() })
                Resource.Success(mockStocks)
            } else {
                Resource.Error(errorMsg)
            }
        }
    }
}