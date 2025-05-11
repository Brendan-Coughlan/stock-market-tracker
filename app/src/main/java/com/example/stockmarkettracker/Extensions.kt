package com.example.stockmarkettracker

import com.example.stockmarkettracker.data.StockDisplay
import com.example.stockmarkettracker.data.StockSnapshot

fun StockSnapshot.toStockDisplay(): StockDisplay {
    return StockDisplay(
        ticker = this.ticker,
        companyName = "Test",
        currentPrice = this.day.c,
        percentChange = this.todaysChangePerc
    )
}
