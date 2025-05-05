package com.example.stockmarkettracker.ui

import TickerItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarkettracker.network.NetworkStockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StockViewModel(
    private val stockRepository: NetworkStockRepository = NetworkStockRepository()
) : ViewModel() {

    private val _tickerList = MutableStateFlow<List<TickerItem>>(emptyList())
    val tickerList: StateFlow<List<TickerItem>> = _tickerList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun searchTickers(query: String) {
        _tickerList.value = emptyList()
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val results = stockRepository.searchTickers(query)
                _tickerList.value = results
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
