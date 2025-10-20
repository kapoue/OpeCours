package com.kapoue.opecours.data.remote

import com.kapoue.opecours.data.remote.dto.AlphaVantageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageApi {
    
    @GET("query")
    suspend fun getQuote(
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): Response<AlphaVantageResponse>
    
    @GET("query")
    suspend fun getDailyData(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String,
        @Query("outputsize") outputSize: String = "compact"
    ): Response<AlphaVantageResponse>
}