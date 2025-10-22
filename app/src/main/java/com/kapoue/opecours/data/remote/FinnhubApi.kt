package com.kapoue.opecours.data.remote

import com.kapoue.opecours.data.remote.dto.FinnhubQuoteResponse
import com.kapoue.opecours.data.remote.dto.FinnhubCandleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Retrofit pour l'API Finnhub
 * Documentation: https://finnhub.io/docs/api
 */
interface FinnhubApi {
    
    /**
     * Récupère le cours actuel d'une action
     * @param symbol Symbole de l'action (ex: "ORA.PA")
     * @param token Clé API Finnhub
     * @return Cours actuel avec variations
     */
    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Response<FinnhubQuoteResponse>
    
    /**
     * Récupère les données historiques (chandelles)
     * @param symbol Symbole de l'action
     * @param resolution Résolution (D = journalier)
     * @param from Timestamp de début
     * @param to Timestamp de fin
     * @param token Clé API Finnhub
     * @return Données historiques sur la période
     */
    @GET("stock/candle")
    suspend fun getCandles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String = "D",
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("token") token: String
    ): Response<FinnhubCandleResponse>
    
    /**
     * Récupère le profil de l'entreprise
     * @param symbol Symbole de l'action
     * @param token Clé API Finnhub
     * @return Informations sur l'entreprise
     */
    @GET("stock/profile2")
    suspend fun getCompanyProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Response<Any> // Pour usage futur
}