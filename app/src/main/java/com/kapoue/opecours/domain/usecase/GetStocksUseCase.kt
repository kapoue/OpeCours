package com.kapoue.opecours.domain.usecase

import com.kapoue.opecours.data.repository.StockRepository
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class GetStocksUseCase @Inject constructor(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<Resource<List<Stock>>> {
        return repository.getStocks()
            .catch { e ->
                emit(Resource.Error("Erreur UseCase: ${e.message}"))
            }
    }
}