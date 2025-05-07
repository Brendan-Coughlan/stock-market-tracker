package com.example.stockmarkettracker

fun TickerItem.toStock(): Stock {
    return Stock(
        ticker = this.ticker,
        companyName = this.name,
        currentPrice = 0.0,      // Placeholder for now
        percentChange = 0.0      // Placeholder for now
    )
}
