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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment


@Composable
fun StockCard(stock: StockDisplay, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),  // consistent outer spacing
        colors = CardDefaults.cardColors(
            containerColor = Color(30, 30, 30)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stock.ticker,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "$%.2f".format(stock.currentPrice),
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "%.2f%%".format(stock.percentChange),
                color = if (stock.percentChange >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                style = MaterialTheme.typography.bodyMedium
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp, vertical = 24.dp) // Adds breathing room
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth(0.4f) // smaller width
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A1F33),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Back To Search")
        }
        Image(
            painter = painterResource(id = R.drawable.watchlist),
            contentDescription = "Bull Market Logo",
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp)
        )

        LazyColumn {
            item {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
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
