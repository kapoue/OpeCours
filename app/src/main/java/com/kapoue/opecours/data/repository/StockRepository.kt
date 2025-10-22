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
import com.kapoue.opecours.util.DebugUtils
import com.kapoue.opecours.util.NetworkUtils
import com.kapoue.opecours.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.util.Calendar
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
        DebugUtils.logInfo("Début de getStocks()")
        emit(Resource.Loading())
        
        // Émettre les données en cache d'abord
        try {
            val cachedStocks = dao.getAllStocks().first().map { it.toDomain() }
            DebugUtils.logInfo("Données en cache trouvées: ${cachedStocks.size} stocks")
            if (cachedStocks.isNotEmpty()) {
                emit(Resource.Success(cachedStocks))
            }
        } catch (e: Exception) {
            DebugUtils.logError("Erreur lors de la lecture du cache", e)
        }
        
        // Puis tenter de rafraîchir depuis l'API
        if (networkUtils.isNetworkAvailable()) {
            DebugUtils.logInfo("Réseau disponible, tentative d'appel API")
            try {
                val stocks = fetchFromApi()
                DebugUtils.logInfo("API appelée avec succès, ${stocks.size} stocks récupérés")
                dao.insertAll(stocks.map { it.toEntity() })
                emit(Resource.Success(stocks))
            } catch (e: HttpException) {
                val errorMsg = "Erreur serveur (${e.code()}): ${e.message()}"
                DebugUtils.logError(errorMsg, e)
                emit(Resource.Error(errorMsg))
            } catch (e: IOException) {
                DebugUtils.logError("Erreur de connexion Internet", e)
                emit(Resource.Error("Erreur de connexion Internet"))
            } catch (e: Exception) {
                val errorMsg = "Erreur lors de la récupération des données: ${e.message}"
                DebugUtils.logError(errorMsg, e)
                emit(Resource.Error(errorMsg))
            }
        } else {
            DebugUtils.logInfo("Pas de réseau disponible")
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
        DebugUtils.logInfo("🔄 Début de fetchFromApi() - Test Finnhub puis fallback Alpha Vantage")
        
        // Essayer Finnhub d'abord
        return try {
            DebugUtils.logInfo("🔄 Tentative avec Finnhub API")
            DebugUtils.logInfo("🔑 Clé Finnhub utilisée: ${Constants.FINNHUB_API_KEY}")
            DebugUtils.logInfo("🌐 URL de base Finnhub: ${Constants.FINNHUB_BASE_URL}")
            
            val activeOperators = Operator.values().filter { it.isActive }
            DebugUtils.logInfo("📊 Opérateurs actifs: ${activeOperators.map { it.displayName }}")
            
            activeOperators.map { operator ->
                fetchStockFromFinnhub(operator)
            }
        } catch (e: Exception) {
            DebugUtils.logError("💥 Échec de l'API Finnhub - Fallback vers Alpha Vantage", e)
            
            // Fallback vers Alpha Vantage
            try {
                DebugUtils.logInfo("🔄 Fallback vers Alpha Vantage API")
                DebugUtils.logInfo("🔑 Clé Alpha Vantage utilisée: ${Constants.ALPHA_VANTAGE_API_KEY}")
                DebugUtils.logInfo("🌐 URL de base Alpha Vantage: ${Constants.ALPHA_VANTAGE_BASE_URL}")
                
                val activeOperators = Operator.values().filter { it.isActive }
                activeOperators.map { operator ->
                    DebugUtils.logInfo("📞 Appel Alpha Vantage pour ${operator.displayName} (${operator.symbol})")
                    val response = alphaVantageApi.getQuote(
                        symbol = operator.symbol,
                        apiKey = Constants.ALPHA_VANTAGE_API_KEY
                    )
                    DebugUtils.logInfo("📡 Réponse HTTP ${response.code()} pour ${operator.displayName}")
                    
                    if (response.isSuccessful) {
                        val body = response.body()
                        DebugUtils.logInfo("📦 Body reçu pour ${operator.displayName}: ${body?.toString()?.take(200)}...")
                        
                        val stockData = body?.let { AlphaVantageMapper.toStock(it, operator) }
                            ?: throw Exception("Données invalides pour ${operator.displayName}")
                        DebugUtils.logInfo("✅ Données Alpha Vantage récupérées pour ${operator.displayName}: ${stockData.currentPrice}€")
                        stockData
                    } else {
                        val errorBody = response.errorBody()?.string()
                        DebugUtils.logError("❌ Erreur HTTP ${response.code()} pour ${operator.displayName}: $errorBody")
                        throw HttpException(response)
                    }
                }
            } catch (fallbackException: Exception) {
                DebugUtils.logError("💥 Échec aussi d'Alpha Vantage", fallbackException)
                
                if (Constants.USE_MOCK_DATA_FALLBACK) {
                    DebugUtils.logInfo("🎭 Fallback vers données mock activé")
                    val mockStocks = MockStockData.getMockStocks().filter { stock ->
                        Operator.values().find { it.symbol == stock.symbol }?.isActive == true
                    }
                    DebugUtils.logInfo("📈 Données mock générées: ${mockStocks.map { "${it.operatorName}: ${it.currentPrice}€" }}")
                    mockStocks
                } else {
                    DebugUtils.logError("❌ Fallback vers données mock désactivé - propagation de l'erreur")
                    throw fallbackException
                }
            }
        }
    }
    
    /**
     * Récupère les données d'un stock depuis Finnhub
     * @param operator L'opérateur télécom
     * @return Stock avec données Finnhub (délai 15 minutes)
     */
    private suspend fun fetchStockFromFinnhub(operator: Operator): Stock {
        DebugUtils.logInfo("📞 Appel Finnhub pour ${operator.displayName} (${operator.symbol})")
        
        // 1. Récupérer le cours actuel
        val quoteResponse = finnhubApi.getQuote(
            symbol = operator.symbol,
            token = Constants.FINNHUB_API_KEY
        )
        
        DebugUtils.logInfo("📡 Réponse HTTP ${quoteResponse.code()} pour ${operator.displayName}")
        
        if (!quoteResponse.isSuccessful) {
            val errorBody = quoteResponse.errorBody()?.string()
            DebugUtils.logError("❌ Erreur HTTP ${quoteResponse.code()} pour ${operator.displayName}: $errorBody")
            throw HttpException(quoteResponse)
        }
        
        val quoteData = quoteResponse.body()
        if (quoteData == null || !quoteData.isValid()) {
            DebugUtils.logError("❌ Données invalides pour ${operator.displayName}")
            throw Exception("Données invalides pour ${operator.displayName}")
        }
        
        DebugUtils.logInfo("📦 Quote reçu pour ${operator.displayName}: ${quoteData.currentPrice}€ (timestamp: ${quoteData.timestamp})")
        
        // 2. Récupérer les données historiques pour le graphique
        val historicalPrices = try {
            val endTime = System.currentTimeMillis() / 1000 // Maintenant
            val startTime = endTime - (7 * 24 * 60 * 60) // 7 jours avant
            
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
                    val prices = candleData.toHistoricalPrices()
                    DebugUtils.logInfo("📈 Données historiques pour ${operator.displayName}: ${prices.size} points")
                    prices
                } else {
                    DebugUtils.logInfo("⚠️ Pas de données historiques pour ${operator.displayName}")
                    emptyList()
                }
            } else {
                DebugUtils.logInfo("⚠️ Erreur lors de la récupération des données historiques pour ${operator.displayName}")
                emptyList()
            }
        } catch (e: Exception) {
            DebugUtils.logInfo("⚠️ Exception lors de la récupération des données historiques pour ${operator.displayName}: ${e.message}")
            emptyList()
        }
        
        // 3. Convertir vers notre modèle Stock
        val stock = quoteData.toStock(operator, historicalPrices)
        
        DebugUtils.logInfo("✅ Données Finnhub récupérées pour ${operator.displayName}: ${stock.currentPrice}€ (${stock.changePercent}%)")
        DebugUtils.logInfo("🕐 Heure des données: ${formatTimestamp(stock.lastUpdate)}")
        
        return stock
    }
    
    /**
     * Formate un timestamp pour l'affichage dans les logs
     */
    private fun formatTimestamp(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)} " +
                "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE).toString().padStart(2, '0')}"
    }
    
    suspend fun refreshStocks(): Resource<List<Stock>> {
        return try {
            if (!networkUtils.isNetworkAvailable()) {
                DebugUtils.logInfo("❌ Pas de réseau disponible")
                if (Constants.USE_MOCK_DATA_FALLBACK) {
                    DebugUtils.logInfo("🎭 Utilisation des données mock (pas de réseau)")
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
            DebugUtils.logError(errorMsg, e)
            if (Constants.USE_MOCK_DATA_FALLBACK) {
                DebugUtils.logInfo("🎭 Fallback vers données mock (erreur HTTP)")
                val mockStocks = MockStockData.getMockStocks()
                dao.insertAll(mockStocks.map { it.toEntity() })
                Resource.Success(mockStocks)
            } else {
                Resource.Error(errorMsg)
            }
        } catch (e: IOException) {
            val errorMsg = "Erreur de connexion Internet"
            DebugUtils.logError(errorMsg, e)
            if (Constants.USE_MOCK_DATA_FALLBACK) {
                DebugUtils.logInfo("🎭 Fallback vers données mock (erreur IO)")
                val mockStocks = MockStockData.getMockStocks()
                dao.insertAll(mockStocks.map { it.toEntity() })
                Resource.Success(mockStocks)
            } else {
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Erreur lors de la récupération des données: ${e.message}"
            DebugUtils.logError(errorMsg, e)
            if (Constants.USE_MOCK_DATA_FALLBACK) {
                DebugUtils.logInfo("🎭 Fallback vers données mock (erreur générale)")
                val mockStocks = MockStockData.getMockStocks()
                dao.insertAll(mockStocks.map { it.toEntity() })
                Resource.Success(mockStocks)
            } else {
                Resource.Error(errorMsg)
            }
        }
    }
}