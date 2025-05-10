package com.example.stockmarkettracker.data

import kotlinx.coroutines.flow.Flow

class TickerRepository(private val dao: TickerDao) {
    fun getAllTickers(): Flow<List<Ticker>> = dao.getAllTickers()

    suspend fun insert(ticker: Ticker) = dao.insert(ticker)
    suspend fun remove(symbol: String) = dao.deleteBySymbol(symbol)
}
