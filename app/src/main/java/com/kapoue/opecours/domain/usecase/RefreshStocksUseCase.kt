package com.kapoue.opecours.domain.usecase

import com.kapoue.opecours.data.repository.StockRepository
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.DebugUtils
import com.kapoue.opecours.util.Resource
import javax.inject.Inject

class RefreshStocksUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(): Resource<List<Stock>> {
        DebugUtils.logInfo("🔄 RefreshStocksUseCase.invoke() appelé")
        return try {
            val result = repository.refreshStocks()
            DebugUtils.logInfo("🔄 RefreshStocksUseCase résultat: ${result::class.simpleName}")
            when (result) {
                is Resource.Success -> {
                    DebugUtils.logInfo("🔄 UseCase Refresh Success: ${result.data?.size ?: 0} stocks")
                }
                is Resource.Error -> {
                    DebugUtils.logError("🔄 UseCase Refresh Error: ${result.message}")
                }
                is Resource.Loading -> {
                    DebugUtils.logInfo("🔄 UseCase Refresh Loading")
                }
            }
            result
        } catch (e: Exception) {
            DebugUtils.logError("💥 Erreur dans RefreshStocksUseCase", e)
            Resource.Error("Erreur RefreshUseCase: ${e.message}")
        }
    }
}