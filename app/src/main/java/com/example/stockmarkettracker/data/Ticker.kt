package com.example.stockmarkettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickers")
data class Ticker (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val symbol: String
)