package com.kapoue.opecours.data.repository

import com.kapoue.opecours.data.local.StockDao
import com.kapoue.opecours.data.mapper.toDomain
import com.kapoue.opecours.data.mapper.toEntity
import com.kapoue.opecours.data.remote.dto.AlphaVantageMapper
import com.kapoue.opecours.data.mock.MockStockData
import com.kapoue.opecours.data.remote.AlphaVantageApi
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val api: AlphaVantageApi,
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
        DebugUtils.logInfo("🔄 Début de fetchFromApi() avec Alpha Vantage")
        DebugUtils.logInfo("🔑 Clé API utilisée: ${Constants.API_KEY}")
        DebugUtils.logInfo("🌐 URL de base: ${Constants.BASE_URL}")
        
        return try {
            val activeOperators = Operator.values().filter { it.isActive }
            DebugUtils.logInfo("📊 Opérateurs actifs: ${activeOperators.map { it.displayName }}")
            
            activeOperators.map { operator ->
                DebugUtils.logInfo("📞 Appel Alpha Vantage pour ${operator.displayName} (${operator.symbol})")
                val response = api.getQuote(
                    symbol = operator.symbol,
                    apiKey = Constants.API_KEY
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
        } catch (e: Exception) {
            DebugUtils.logError("💥 Échec de l'API Alpha Vantage", e)
            
            if (Constants.USE_MOCK_DATA_FALLBACK) {
                DebugUtils.logInfo("🎭 Fallback vers données mock activé")
                val mockStocks = MockStockData.getMockStocks().filter { stock ->
                    Operator.values().find { it.symbol == stock.symbol }?.isActive == true
                }
                DebugUtils.logInfo("📈 Données mock générées: ${mockStocks.map { "${it.operatorName}: ${it.currentPrice}€" }}")
                mockStocks
            } else {
                DebugUtils.logError("❌ Fallback vers données mock désactivé - propagation de l'erreur")
                throw e
            }
        }
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