package com.kapoue.opecours.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StockResponse(
    @SerializedName("chart")
    val chart: ChartWrapper
)

data class ChartWrapper(
    @SerializedName("result")
    val result: List<ChartResult>? = null,
    @SerializedName("error")
    val error: ErrorInfo? = null
)

data class ChartResult(
    @SerializedName("meta")
    val meta: MetaData,
    @SerializedName("timestamp")
    val timestamp: List<Long>? = null,
    @SerializedName("indicators")
    val indicators: Indicators
)

data class MetaData(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("regularMarketPrice")
    val regularMarketPrice: Double,
    @SerializedName("previousClose")
    val previousClose: Double,
    @SerializedName("chartPreviousClose")
    val chartPreviousClose: Double? = null,
    @SerializedName("regularMarketOpen")
    val regularMarketOpen: Double? = null,
    @SerializedName("regularMarketVolume")
    val regularMarketVolume: Long? = null
)

data class Indicators(
    @SerializedName("quote")
    val quote: List<Quote>? = null
)

data class Quote(
    @SerializedName("open")
    val open: List<Double?>? = null,
    @SerializedName("close")
    val close: List<Double?>? = null,
    @SerializedName("high")
    val high: List<Double?>? = null,
    @SerializedName("low")
    val low: List<Double?>? = null,
    @SerializedName("volume")
    val volume: List<Long?>? = null
)

data class ErrorInfo(
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String
)