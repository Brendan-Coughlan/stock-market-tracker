package com.example.stockmarkettracker.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stockmarkettracker.data.PriceAlert
import com.example.stockmarkettracker.data.StockDisplay
import com.example.stockmarkettracker.data.StockSummary
import com.example.stockmarkettracker.data.Ticker
import com.example.stockmarkettracker.data.TickerRepository
import com.example.stockmarkettracker.data.AlertRepository
import com.example.stockmarkettracker.network.NetworkStockRepository
import com.example.stockmarkettracker.toStockDisplay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StockViewModelFactory(
    private val networkRepository: NetworkStockRepository,
    private val tickerRepository: TickerRepository,
    private val alertRepository: AlertRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockViewModel(networkRepository, tickerRepository, alertRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StockViewModel(
    private val stockRepository: NetworkStockRepository,
    private val tickerRepository: TickerRepository,
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _stockSummaryList = MutableStateFlow<List<StockSummary>>(emptyList())
    val stockSummaryList: StateFlow<List<StockSummary>> = _stockSummaryList

    private val _isSearchLoading = MutableStateFlow(false)
    val isSearchLoading: StateFlow<Boolean> = _isSearchLoading

    private val _watchlistDisplay = MutableStateFlow<List<StockDisplay>>(emptyList())
    val watchlistDisplay: StateFlow<List<StockDisplay>> = _watchlistDisplay

    private val _isWatchlistLoading = MutableStateFlow(false)
    val isWatchlistLoading: StateFlow<Boolean> = _isWatchlistLoading

    private val _isAlertLoading = MutableStateFlow(false)
    val isAlertLoading: StateFlow<Boolean> = _isAlertLoading

    init {
        refreshWatchlist()
        viewModelScope.launch {
            alertRepository.getAlerts().first()
            _isAlertLoading.value = false
        }
    }

    val watchlist: StateFlow<List<Ticker>> = tickerRepository
        .getAllTickers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val alerts: StateFlow<List<PriceAlert>> = alertRepository
        .getAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun searchTickers(query: String) {
        _stockSummaryList.value = emptyList()
        _isSearchLoading.value = true
        viewModelScope.launch {
            try {
                val results = stockRepository.searchTickers(query)
                _stockSummaryList.value = results
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSearchLoading.value = false
            }
        }
    }

    fun addToWatchlist(tickerSymbol: String) {
        viewModelScope.launch {
            val ticker = Ticker(symbol = tickerSymbol)
            tickerRepository.insert(ticker)
            refreshWatchlist()
        }
    }

    fun removeFromWatchlist(tickerSymbol: String) {
        viewModelScope.launch {
            tickerRepository.remove(tickerSymbol)
            refreshWatchlist()
        }
    }

    fun refreshWatchlist() {
        _isWatchlistLoading.value = true
        viewModelScope.launch {
            try {
                val localTickers = tickerRepository.getAllTickers().first()
                if (localTickers.isNotEmpty()) {
                    val symbols = localTickers.joinToString(",") { it.symbol }
                    val response = StockApi.retrofitService.getSnapshotTickers(
                        tickers = symbols,
                        apiKey = "HpgP9ZvVg92ynx9g5xThMY3YrH3ZYP1b"
                    )
                    _watchlistDisplay.value = response.tickers.map { it.toStockDisplay() }
                } else {
                    _watchlistDisplay.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("WatchlistVM", "API Failed: ${e.message}")
            } finally {
                _isWatchlistLoading.value = false
            }
        }
    }

    fun addAlert(symbol: String, targetPrice: Double, isAbove: Boolean) {
        viewModelScope.launch {
            alertRepository.addAlert(PriceAlert(symbol = symbol, targetPrice = targetPrice, isAbove = isAbove))
        }
    }

    fun removeAlert(alert: PriceAlert) {
        viewModelScope.launch {
            alertRepository.removeAlert(alert)
        }
    }
}
