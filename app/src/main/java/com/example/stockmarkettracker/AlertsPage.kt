package com.example.stockmarkettracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockmarkettracker.data.AlertRepository
import com.example.stockmarkettracker.data.TickerRepository
import com.example.stockmarkettracker.network.NetworkStockRepository
import com.example.stockmarkettracker.ui.StockViewModel
import com.example.stockmarkettracker.ui.StockViewModelFactory
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon

@Composable
fun AlertsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = StocksDatabase.getDatabase(context)
    val tickerRepo = TickerRepository(db.tickerDao())
    val networkRepo = NetworkStockRepository()
    val alertRepo = AlertRepository(db.priceAlertDao())

    val viewModel: StockViewModel = viewModel(
        factory = StockViewModelFactory(networkRepo, tickerRepo, alertRepo)
    )

    val alerts = viewModel.alerts.collectAsState().value
    val isLoading = viewModel.isAlertLoading.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Price Alerts",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (alerts.isEmpty()) {
            Text(
                text = "No alerts set yet.",
                fontSize = 20.sp,
                color = Color.DarkGray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            alerts.forEach { alert ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(alert.symbol, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                text = "Alert when ${if (alert.isAbove) "above" else "below"} $${alert.targetPrice}",
                                color = Color.Gray
                            )
                        }
                        Button(
                            onClick = { viewModel.removeAlert(alert) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate(Routes.SEARCH) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back to Search")
        }
    }
}
