package com.kapoue.opecours.domain.usecase

import com.kapoue.opecours.data.repository.StockRepository
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.DebugUtils
import com.kapoue.opecours.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetStocksUseCase @Inject constructor(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<Resource<List<Stock>>> {
        DebugUtils.logInfo("🎯 GetStocksUseCase.invoke() appelé")
        return repository.getStocks()
            .onEach { resource ->
                DebugUtils.logInfo("🎯 GetStocksUseCase émission: ${resource::class.simpleName}")
                when (resource) {
                    is Resource.Success -> {
                        DebugUtils.logInfo("🎯 UseCase Success: ${resource.data?.size ?: 0} stocks")
                    }
                    is Resource.Error -> {
                        DebugUtils.logError("🎯 UseCase Error: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        DebugUtils.logInfo("🎯 UseCase Loading")
                    }
                }
            }
            .catch { e ->
                DebugUtils.logError("💥 Erreur dans GetStocksUseCase", e)
                emit(Resource.Error("Erreur UseCase: ${e.message}"))
            }
    }
}