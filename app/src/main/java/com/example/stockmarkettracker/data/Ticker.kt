package com.example.stockmarkettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickers")
data class Ticker (
    @PrimaryKey val symbol: String
)