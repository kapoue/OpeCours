package com.kapoue.opecours.domain.usecase

import com.kapoue.opecours.data.repository.StockRepository
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStocksUseCase @Inject constructor(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<Resource<List<Stock>>> {
        return repository.getStocks()
    }
}