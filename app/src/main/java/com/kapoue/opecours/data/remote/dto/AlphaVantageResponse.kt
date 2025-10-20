package com.kapoue.opecours.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AlphaVantageResponse(
    @SerializedName("Global Quote")
    val globalQuote: GlobalQuote? = null,
    
    @SerializedName("Time Series (Daily)")
    val timeSeries: Map<String, DailyData>? = null,
    
    @SerializedName("Error Message")
    val errorMessage: String? = null,
    
    @SerializedName("Note")
    val note: String? = null
)

data class GlobalQuote(
    @SerializedName("01. symbol")
    val symbol: String,
    
    @SerializedName("02. open")
    val open: String,
    
    @SerializedName("03. high")
    val high: String,
    
    @SerializedName("04. low")
    val low: String,
    
    @SerializedName("05. price")
    val price: String,
    
    @SerializedName("06. volume")
    val volume: String,
    
    @SerializedName("07. latest trading day")
    val latestTradingDay: String,
    
    @SerializedName("08. previous close")
    val previousClose: String,
    
    @SerializedName("09. change")
    val change: String,
    
    @SerializedName("10. change percent")
    val changePercent: String
)

data class DailyData(
    @SerializedName("1. open")
    val open: String,
    
    @SerializedName("2. high")
    val high: String,
    
    @SerializedName("3. low")
    val low: String,
    
    @SerializedName("4. close")
    val close: String,
    
    @SerializedName("5. volume")
    val volume: String
)