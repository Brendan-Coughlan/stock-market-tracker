package com.example.stockmarkettracker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class WatchlistViewModel : ViewModel() {

    private val _stocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> = _stocks

    init {
        loadWatchlist()
    }

    private fun loadWatchlist() {
        val watchlistTickers = listOf("AAPL", "TSLA", "MSFT") // You can make this dynamic later

        viewModelScope.launch {
            try {
                val allResults = mutableListOf<Stock>()
                for (ticker in watchlistTickers) {
                    val response = StockApi.retrofitService.getTickers(
                        search = ticker,
                        apiKey = "HpgP9ZvVg92ynx9g5xThMY3YrH3ZYP1b"
                    )
                    allResults += response.results.map { it.toStock() }
                }
                _stocks.value = allResults
            } catch (e: Exception) {
                Log.e("WatchlistVM", "API Failed: ${e.message}")
            }
        }
    }
}
