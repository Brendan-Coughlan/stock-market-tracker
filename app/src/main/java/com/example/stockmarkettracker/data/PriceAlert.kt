package com.example.stockmarkettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_alert")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val symbol: String,
    val targetPrice: Double,
    val isAbove: Boolean
)