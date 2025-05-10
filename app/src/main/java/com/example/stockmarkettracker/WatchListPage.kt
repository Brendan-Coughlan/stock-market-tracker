package com.example.stockmarkettracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.stockmarkettracker.data.AlertRepository
import com.example.stockmarkettracker.data.StockDisplay
import com.example.stockmarkettracker.data.TickerRepository
import com.example.stockmarkettracker.network.NetworkStockRepository
import com.example.stockmarkettracker.ui.StockViewModel
import com.example.stockmarkettracker.ui.StockViewModelFactory

@Composable
fun StockCard(stock: StockDisplay, modifier: Modifier = Modifier) {
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
            Text(text = "$%.2f".format(stock.currentPrice))
            Text(
                text = "%.2f%%".format(stock.percentChange),
                color = if (stock.percentChange >= 0) Color.Green else Color.Red
            )
        }
    }
}

@Composable
fun WatchlistScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val db = StocksDatabase.getDatabase(context)
    val tickerRepo = TickerRepository(db.tickerDao())
    val networkRepo = NetworkStockRepository()
    val alertRepo = AlertRepository(db.priceAlertDao())

    val viewModel: StockViewModel = viewModel(
        factory = StockViewModelFactory(networkRepo, tickerRepo, alertRepo)
    )

    val isLoading by viewModel.isWatchlistLoading.collectAsState()
    val watchlistItems = viewModel.watchlistDisplay.collectAsState().value

    Column(modifier = modifier.padding(16.dp)) {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Back")
        }

        LazyColumn {
            item {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0, 0, 0),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            items(watchlistItems) { stock ->
                StockCard(stock = stock)
            }
        }
    }
}
