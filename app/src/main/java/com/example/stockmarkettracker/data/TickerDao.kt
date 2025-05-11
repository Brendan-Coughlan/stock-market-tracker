package com.example.stockmarkettracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.stockmarkettracker.data.Ticker

@Dao
interface TickerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ticker: Ticker)

    @Delete
    suspend fun delete(ticker: Ticker)

    @Query("Select * FROM tickers ORDER BY symbol ASC")
    fun getAllTickers(): Flow<List<Ticker>>

    @Query("DELETE FROM tickers WHERE symbol = :symbol")
    suspend fun deleteBySymbol(symbol: String)
}