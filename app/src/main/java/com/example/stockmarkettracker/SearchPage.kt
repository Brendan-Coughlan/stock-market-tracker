import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stockmarkettracker.Routes
import com.example.stockmarkettracker.data.AlertRepository
import com.example.stockmarkettracker.data.StockSummary
import com.example.stockmarkettracker.data.TickerRepository
import com.example.stockmarkettracker.network.NetworkStockRepository
import com.example.stockmarkettracker.ui.StockViewModel
import com.example.stockmarkettracker.ui.StockViewModelFactory

@Composable
fun TickerCard(stockSummary: StockSummary,
               isWatchlisted: Boolean,
               onToggleWatchlist: () -> Unit,
               onSetAlertClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(1.dp, Color(200, 200, 200), RoundedCornerShape(8.dp))
            .background(Color(15, 15, 15)),
        colors = CardDefaults.cardColors(
            containerColor = Color(15, 15, 15) // Slightly lighter than black
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stockSummary.ticker,
                color = Color(200, 200, 200),
                fontSize = 24.sp
            )
            Text(
                text = stockSummary.name,
                color = Color.Gray,
                fontSize = 16.sp
            )
            Text(
                text = "Market: ${stockSummary.market} | Exchange: ${stockSummary.primaryExchange}",
                color = Color.LightGray,
                fontSize = 14.sp
            )
            Button(
                onClick = onToggleWatchlist,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = if (isWatchlisted) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null
                )
            }
            Button(
                onClick = { onSetAlertClick(stockSummary.ticker) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = StocksDatabase.getDatabase(context)
    val tickerRepo = TickerRepository(db.tickerDao())
    val networkRepo = NetworkStockRepository()
    val alertRepo = AlertRepository(db.priceAlertDao())

    val viewModel: StockViewModel = viewModel(
        factory = StockViewModelFactory(networkRepo, tickerRepo, alertRepo)
    )

    val watchlist by viewModel.watchlist.collectAsState()
    val stockSummaryList by viewModel.stockSummaryList.collectAsState()
    val isLoading by viewModel.isSearchLoading.collectAsState()
    val searchText = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val selectedTicker = remember { mutableStateOf<String?>(null) }

    Column(
        modifier.fillMaxSize().background(Color(15, 15, 15)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Search",
            color = Color(200, 200, 200),
            textAlign = TextAlign.Center,
            fontSize = 50.sp,
            fontFamily = FontFamily.SansSerif,
            modifier = modifier
        )
        TextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            textStyle = TextStyle(
                fontSize = 25.sp,
                color = Color(200, 200, 200),
                textAlign = TextAlign.Center
            ),
            modifier = modifier.width(250.dp)
                .border(
                    width = 2.dp,
                    color = Color(200, 200, 200),
                    shape = RoundedCornerShape(20.dp)
                ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(200, 200, 200)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.searchTickers(searchText.value)
                }
            )
        )
        Button(
            onClick = {
                viewModel.searchTickers(searchText.value)
            },
            modifier = Modifier.border(2.dp, Color(200, 200, 200), RoundedCornerShape(20.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(15, 15, 15),  // Button background
                contentColor = Color(200, 200, 200)  // Text color
            )
        ) {
            Text("Search", fontSize = 20.sp)
        }
        Button(
            onClick = { navController.navigate(Routes.WATCHLIST) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Watchlist")
        }

        Button(
            onClick = { navController.navigate(Routes.ALERTS) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Alerts")
        }

        LazyColumn(modifier = Modifier.padding(top = 20.dp)) {
            item {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(200, 200, 200),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            items(stockSummaryList.toList()) { result ->
                val isInWatchlist = watchlist.any { it.symbol == result.ticker }
                TickerCard(
                    stockSummary = result,
                    isWatchlisted = isInWatchlist,
                    onToggleWatchlist = {
                        if (isInWatchlist) {
                            viewModel.removeFromWatchlist(result.ticker)
                        } else {
                            viewModel.addToWatchlist(result.ticker)
                        }
                    },
                    onSetAlertClick = { ticker ->
                        selectedTicker.value = ticker
                        showDialog.value = true
                    }
                )
            }
        }
        if (showDialog.value && selectedTicker.value != null) {
            showAlertDialog(
                ticker = selectedTicker.value!!,
                onConfirm = { price, isAbove ->
                    viewModel.addAlert(selectedTicker.value!!, price, isAbove)
                    showDialog.value = false
                },
                onDismiss = { showDialog.value = false }
            )
        }
    }
}
@Composable
fun showAlertDialog(
    ticker: String,
    onConfirm: (Double, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val priceText = remember { mutableStateOf("") }
    val isAbove = remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val price = priceText.value.toDoubleOrNull()
                if (price != null) {
                    onConfirm(price, isAbove.value)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Set Price Alert for $ticker") },
        text = {
            Column {
                TextField(
                    value = priceText.value,
                    onValueChange = { priceText.value = it },
                    label = { Text("Target Price") }
                )
//                Row {
//                    Text("Alert when price is ")
//                    Text(if (isAbove.value) "above" else "below")
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(onClick = { isAbove.value = !isAbove.value }) {
//                        Text(if (isAbove.value) "Change to Below" else "Change to Above")
//                    }
//                }
            }
        }
    )
}

