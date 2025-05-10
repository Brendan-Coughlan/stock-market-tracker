package com.example.stockmarkettracker.data

import com.google.gson.annotations.SerializedName

data class StockSummaryResponse(
    val results: List<StockSummary>,
    val status: String,
    @SerializedName("request_id")
    val requestId: String,
    val count: Int
)

data class StockSummary(
    val ticker: String,
    val name: String,
    val market: String,
    val locale: String,
    @SerializedName("primary_exchange")
    val primaryExchange: String,
    val type: String,
    val active: Boolean,
    @SerializedName("currency_name")
    val currencyName: String,
    val cik: String? = null,
    @SerializedName("composite_figi")
    val compositeFigi: String,
    @SerializedName("share_class_figi")
    val shareClassFigi: String,
    @SerializedName("last_updated_utc")
    val lastUpdatedUtc: String
)

data class StockSnapshotResponse(
    val tickers: List<StockSnapshot>,
    val status: String,
    @SerializedName("request_id")
    val requestId: String,
    val count: Int
)

data class StockSnapshot(
    val ticker: String,
    val todaysChangePerc: Double,
    val todaysChange: Double,
    val updated: Long,
    val day: StockDayData,
    val min: StockMinuteData,
    val prevDay: StockDayData
)

data class StockDayData(
    val o: Double,  // open
    val h: Double,  // high
    val l: Double,  // low
    val c: Double,  // close
    val v: Long?,   // volume
    val vw: Double  // volume-weighted avg price
)

data class StockMinuteData(
    val av: Long?,  // avg volume
    val t: Long?,   // timestamp
    val n: Int,     // # of trades
    val o: Double,
    val h: Double,
    val l: Double,
    val c: Double,
    val v: Long?,
    val vw: Double
)

data class StockDisplay(
    val ticker: String,
    val companyName: String,
    val currentPrice: Double,
    val percentChange: Double
)
