package com.kapoue.opecours.domain.usecase

import com.kapoue.opecours.data.repository.StockRepository
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.util.Resource
import javax.inject.Inject

class RefreshStocksUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(): Resource<List<Stock>> {
        return try {
            repository.refreshStocks()
        } catch (e: Exception) {
            Resource.Error("Erreur RefreshUseCase: ${e.message}")
        }
    }
}