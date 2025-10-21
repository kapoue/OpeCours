package com.kapoue.opecours.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.domain.usecase.GetStocksUseCase
import com.kapoue.opecours.domain.usecase.RefreshStocksUseCase
import com.kapoue.opecours.util.Constants
import com.kapoue.opecours.util.DateUtils
import com.kapoue.opecours.util.DebugUtils
import com.kapoue.opecours.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getStocksUseCase: GetStocksUseCase,
    private val refreshStocksUseCase: RefreshStocksUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(StocksState())
    val state: StateFlow<StocksState> = _state.asStateFlow()
    
    private var autoRefreshJob: Job? = null
    
    init {
        DebugUtils.logInfo("🚀 MainViewModel initialisé")
        loadStocks()
        startAutoRefresh()
    }
    
    private fun loadStocks() {
        DebugUtils.logInfo("📱 MainViewModel.loadStocks() appelé")
        viewModelScope.launch {
            try {
                DebugUtils.logInfo("🔄 Début de la collecte des stocks via UseCase")
                getStocksUseCase().collect { resource ->
                    DebugUtils.logInfo("📦 Resource reçu: ${resource::class.simpleName}")
                    when (resource) {
                        is Resource.Loading -> {
                            DebugUtils.logInfo("⏳ État: Loading")
                            _state.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            DebugUtils.logInfo("✅ État: Success avec ${resource.data?.size ?: 0} stocks")
                            resource.data?.forEach { stock ->
                                DebugUtils.logInfo("📊 Stock: ${stock.operatorName} = ${stock.currentPrice}€")
                            }
                            _state.update {
                                it.copy(
                                    stocks = resource.data ?: emptyList(),
                                    isLoading = false,
                                    isRefreshing = false,
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            DebugUtils.logError("❌ État: Error - ${resource.message}")
                            _state.update {
                                it.copy(
                                    error = resource.message,
                                    isLoading = false,
                                    isRefreshing = false
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                DebugUtils.logError("💥 Erreur dans MainViewModel.loadStocks()", e)
                _state.update {
                    it.copy(
                        error = "Erreur dans ViewModel: ${e.message}",
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }
    
    fun refresh() {
        DebugUtils.logInfo("🔄 MainViewModel.refresh() appelé par l'utilisateur")
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            try {
                DebugUtils.logInfo("🔄 Appel du RefreshStocksUseCase")
                val result = refreshStocksUseCase()
                DebugUtils.logInfo("📦 RefreshStocksUseCase résultat: ${result::class.simpleName}")
                when (result) {
                    is Resource.Success -> {
                        DebugUtils.logInfo("✅ Refresh Success avec ${result.data?.size ?: 0} stocks")
                        _state.update {
                            it.copy(
                                stocks = result.data ?: emptyList(),
                                isRefreshing = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        DebugUtils.logError("❌ Refresh Error: ${result.message}")
                        _state.update {
                            it.copy(
                                error = result.message,
                                isRefreshing = false
                            )
                        }
                    }
                    is Resource.Loading -> {
                        DebugUtils.logInfo("⏳ Refresh Loading - garde isRefreshing = true")
                    }
                }
            } catch (e: Exception) {
                DebugUtils.logError("💥 Erreur dans MainViewModel.refresh()", e)
                _state.update {
                    it.copy(
                        error = "Erreur refresh: ${e.message}",
                        isRefreshing = false
                    )
                }
            }
        }
    }
    
    private fun startAutoRefresh() {
        DebugUtils.logInfo("⏰ Démarrage de l'auto-refresh (5 minutes)")
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(Constants.AUTO_REFRESH_INTERVAL_MS)
                if (isMarketOpen()) {
                    DebugUtils.logInfo("🕐 Auto-refresh déclenché (marché ouvert)")
                    refresh()
                } else {
                    DebugUtils.logInfo("🕐 Auto-refresh ignoré (marché fermé)")
                }
            }
        }
    }
    
    private fun isMarketOpen(): Boolean {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        
        // Marché ouvert lun-ven, 9h-17h30
        return dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY &&
               (hour > Constants.MARKET_OPEN_HOUR || 
                (hour == Constants.MARKET_OPEN_HOUR && minute >= Constants.MARKET_OPEN_MINUTE)) &&
               (hour < Constants.MARKET_CLOSE_HOUR || 
                (hour == Constants.MARKET_CLOSE_HOUR && minute <= Constants.MARKET_CLOSE_MINUTE))
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        DebugUtils.logInfo("🧹 MainViewModel.onCleared() - nettoyage")
        autoRefreshJob?.cancel()
    }
}

data class StocksState(
    val stocks: List<Stock> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)