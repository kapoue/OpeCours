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
        DebugUtils.logInfo("Début de fetchFromApi() avec Alpha Vantage")
        return try {
            Operator.values()
                .filter { it.isActive } // Ne traiter que les opérateurs actifs
                .map { operator ->
                    DebugUtils.logInfo("Appel Alpha Vantage pour ${operator.displayName} (${operator.symbol})")
                    val response = api.getQuote(
                        symbol = operator.symbol,
                        apiKey = Constants.API_KEY
                    )
                    if (response.isSuccessful) {
                        val stockData = response.body()?.let { AlphaVantageMapper.toStock(it, operator) }
                            ?: throw Exception("Données invalides pour ${operator.displayName}")
                        DebugUtils.logInfo("Données Alpha Vantage récupérées pour ${operator.displayName}: ${stockData.currentPrice}€")
                        stockData
                    } else {
                        DebugUtils.logError("Erreur HTTP ${response.code()} pour ${operator.displayName}")
                        throw HttpException(response)
                    }
                }
        } catch (e: Exception) {
            DebugUtils.logError("Échec de l'API Alpha Vantage, utilisation des données mock", e)
            // En cas d'échec de l'API, utiliser les données mock (seulement les actifs)
            MockStockData.getMockStocks().filter { stock ->
                Operator.values().find { it.symbol == stock.symbol }?.isActive == true
            }
        }
    }
    
    suspend fun refreshStocks(): Resource<List<Stock>> {
        return try {
            if (!networkUtils.isNetworkAvailable()) {
                DebugUtils.logInfo("Pas de réseau, utilisation des données mock")
                val mockStocks = MockStockData.getMockStocks()
                dao.insertAll(mockStocks.map { it.toEntity() })
                return Resource.Success(mockStocks)
            }
            
            val stocks = fetchFromApi()
            dao.insertAll(stocks.map { it.toEntity() })
            Resource.Success(stocks)
        } catch (e: HttpException) {
            DebugUtils.logError("Erreur HTTP, fallback vers mock", e)
            val mockStocks = MockStockData.getMockStocks()
            dao.insertAll(mockStocks.map { it.toEntity() })
            Resource.Success(mockStocks)
        } catch (e: IOException) {
            DebugUtils.logError("Erreur IO, fallback vers mock", e)
            val mockStocks = MockStockData.getMockStocks()
            dao.insertAll(mockStocks.map { it.toEntity() })
            Resource.Success(mockStocks)
        } catch (e: Exception) {
            DebugUtils.logError("Erreur générale, fallback vers mock", e)
            val mockStocks = MockStockData.getMockStocks()
            dao.insertAll(mockStocks.map { it.toEntity() })
            Resource.Success(mockStocks)
        }
    }
}