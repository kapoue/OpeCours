package com.kapoue.opecours.data.remote

import com.kapoue.opecours.data.remote.dto.StockResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YahooFinanceApi {
    @GET("v8/finance/chart/{symbol}")
    suspend fun getStockData(
        @Path("symbol") symbol: String,
        @Query("interval") interval: String = "1d",
        @Query("range") range: String = "5d"
    ): Response<StockResponse>
}