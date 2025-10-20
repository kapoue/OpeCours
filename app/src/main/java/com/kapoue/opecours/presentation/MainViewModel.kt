package com.kapoue.opecours.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.domain.usecase.GetStocksUseCase
import com.kapoue.opecours.domain.usecase.RefreshStocksUseCase
import com.kapoue.opecours.util.Constants
import com.kapoue.opecours.util.DateUtils
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
        loadStocks()
        startAutoRefresh()
    }
    
    private fun loadStocks() {
        viewModelScope.launch {
            getStocksUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
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
        }
    }
    
    fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val result = refreshStocksUseCase()
            when (result) {
                is Resource.Success -> {
                    _state.update { 
                        it.copy(
                            stocks = result.data ?: emptyList(),
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { 
                        it.copy(
                            error = result.message,
                            isRefreshing = false
                        )
                    }
                }
                is Resource.Loading -> {
                    // Ne rien faire, on garde isRefreshing = true
                }
            }
        }
    }
    
    private fun startAutoRefresh() {
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(Constants.AUTO_REFRESH_INTERVAL_MS)
                if (isMarketOpen()) {
                    refresh()
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
        autoRefreshJob?.cancel()
    }
}

data class StocksState(
    val stocks: List<Stock> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)