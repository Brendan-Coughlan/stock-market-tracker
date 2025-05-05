package com.example.stockmarkettracker.network

import StockApiService
import TickerDetailsResponse
import TickerItem

// Define the interface
interface StockRepository {
    suspend fun searchTickers(query: String): List<TickerItem>
    suspend fun getTickerDetails(tickers: String): TickerDetailsResponse
}

class NetworkStockRepository(
    private val stockApiService: StockApiService = StockApi.retrofitService
) : StockRepository {

    private val apiKey = "HpgP9ZvVg92ynx9g5xThMY3YrH3ZYP1b"

    override suspend fun searchTickers(query: String): List<TickerItem> {
        return stockApiService.getTickers(search = query, apiKey = apiKey).results
    }

    override suspend fun getTickerDetails(tickers: String): TickerDetailsResponse {
        return stockApiService.getSnapshotTickers(tickers = tickers, apiKey = apiKey)
    }
}
