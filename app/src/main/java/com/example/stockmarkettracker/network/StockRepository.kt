package com.example.stockmarkettracker.network

import StockApiService
import com.example.stockmarkettracker.data.StockSnapshotResponse
import com.example.stockmarkettracker.data.StockSummary

// Define the interface
interface StockRepository {
    suspend fun searchTickers(query: String): List<StockSummary>
    suspend fun getTickerDetails(tickers: String): StockSnapshotResponse
}

class NetworkStockRepository(
    private val stockApiService: StockApiService = StockApi.retrofitService
) : StockRepository {

    private val apiKey = "HpgP9ZvVg92ynx9g5xThMY3YrH3ZYP1b"

    override suspend fun searchTickers(query: String): List<StockSummary> {
        return stockApiService.getTickers(search = query, apiKey = apiKey).results
    }

    override suspend fun getTickerDetails(tickers: String): StockSnapshotResponse {
        return stockApiService.getSnapshotTickers(tickers = tickers, apiKey = apiKey)
    }
}
