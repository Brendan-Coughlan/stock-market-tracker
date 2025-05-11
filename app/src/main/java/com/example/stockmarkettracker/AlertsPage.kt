package com.example.stockmarkettracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockmarkettracker.data.AlertRepository
import com.example.stockmarkettracker.data.TickerRepository
import com.example.stockmarkettracker.network.NetworkStockRepository
import com.example.stockmarkettracker.ui.StockViewModel
import com.example.stockmarkettracker.ui.StockViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    val visibleAlerts = remember { mutableStateMapOf<Int, Boolean>() }
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.alert),
            contentDescription = "Bull Market Logo",
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp)
        )

        Text(
            text = "Price Alerts",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = Color.White)
        } else if (alerts.isEmpty()) {
            Text(
                text = "No alerts set yet.",
                fontSize = 20.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(alerts, key = { it.id }) { alert ->
                    val isVisible = visibleAlerts.getOrPut(alert.id) { true }

                    AnimatedVisibility(
                        visible = isVisible,
                        exit = scaleOut(tween(300)) + fadeOut(tween(300))
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(25, 25, 25)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(alert.symbol, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                                    Text(
                                        text = "Alert when ${if (alert.isAbove) "above" else "below"} $${alert.targetPrice}",
                                        color = Color.LightGray
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        visibleAlerts[alert.id] = false
                                        coroutineScope.launch {
                                            delay(300)
                                            viewModel.removeAlert(alert)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Alert",
                                        tint = Color.Red
                                    )
                                }


                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Routes.SEARCH) },
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A1F33),
                contentColor = Color.White
            )
        ) {
            Text("Back to Search")
        }
    }
}
