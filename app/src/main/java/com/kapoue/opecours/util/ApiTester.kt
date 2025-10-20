package com.kapoue.opecours.util

import android.util.Log
import com.kapoue.opecours.data.mapper.toStock
import com.kapoue.opecours.data.remote.YahooFinanceApi
import com.kapoue.opecours.domain.model.Operator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiTester @Inject constructor(
    private val api: YahooFinanceApi
) {
    
    fun testApiCall() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("ApiTester", "=== DÉBUT TEST API ===")
                
                // Test avec Orange
                val operator = Operator.ORANGE
                Log.d("ApiTester", "Test pour ${operator.displayName} (${operator.symbol})")
                
                val response = api.getStockData(operator.symbol)
                Log.d("ApiTester", "Code de réponse: ${response.code()}")
                Log.d("ApiTester", "Succès: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("ApiTester", "Body reçu: $body")
                        
                        // Essayer de parser
                        val stock = body.toStock(operator)
                        Log.d("ApiTester", "Stock parsé: ${stock.operatorName} - ${stock.currentPrice}€")
                        Log.d("ApiTester", "Variation: ${stock.changePercent}%")
                        Log.d("ApiTester", "Données historiques: ${stock.historicalPrices.size} points")
                        
                        Log.d("ApiTester", "=== TEST RÉUSSI ===")
                    } else {
                        Log.e("ApiTester", "Body null")
                    }
                } else {
                    Log.e("ApiTester", "Erreur HTTP: ${response.code()} - ${response.message()}")
                    Log.e("ApiTester", "Error body: ${response.errorBody()?.string()}")
                }
                
            } catch (e: Exception) {
                Log.e("ApiTester", "Erreur lors du test API", e)
            }
        }
    }
}