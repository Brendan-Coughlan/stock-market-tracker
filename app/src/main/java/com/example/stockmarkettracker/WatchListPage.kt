package com.example.stockmarkettracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController

data class Stock(
    val ticker: String,
    val companyName: String,
    val currentPrice: Double,
    val percentChange: Double
)

@Composable
fun WatchlistScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: WatchlistViewModel = viewModel()
) {
    val watchlist = viewModel.stocks.collectAsState().value

    Column(modifier = modifier.padding(16.dp)) {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Back")
        }

        // Now LazyColumn is inside the same Column, so layout works top to bottom
        LazyColumn {
            items(watchlist) { stock ->
                StockCard(stock = stock)
            }
        }
    }
}



@Composable
fun StockCard(stock: Stock, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stock.ticker,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(text = stock.companyName)
            Text(text = "$${stock.currentPrice}")
            Text(
                text = "${stock.percentChange}%",
                color = if (stock.percentChange >= 0) Color.Green else Color.Red
            )
        }
    }
}
