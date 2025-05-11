import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.stockmarkettracker.R

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextDecoration


import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle


import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset



@Composable
fun TickerCard(
    stockSummary: StockSummary,
    isWatchlisted: Boolean,
    onToggleWatchlist: () -> Unit,
    onSetAlertClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(25, 25, 25)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stockSummary.ticker,
                color = Color.White,
                fontSize = 22.sp,
                style = MaterialTheme.typography.titleMedium
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
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Button(
                    onClick = onToggleWatchlist,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    elevation = ButtonDefaults.buttonElevation(6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (isWatchlisted) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { onSetAlertClick(stockSummary.ticker) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    elevation = ButtonDefaults.buttonElevation(6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null
                    )
                }
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
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.bull),
            contentDescription = "Bull Market Logo",
            modifier = Modifier
                .size(200.dp) // Adjust the size
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp)
        )

        Text(
            text = "KotlinTrade",
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Cursive,
            color = Color(0xFF6EE7B7),
            style = TextStyle(

            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )


        TextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            textStyle = TextStyle(
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(15.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFF0A1F33),
                cursorColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
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
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(0.7f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A1F33),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.navigate(Routes.WATCHLIST) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A1F33),
                    contentColor = Color.White
                )
            ) {
                Text("Watchlist")
            }

            Button(
                onClick = { navController.navigate(Routes.ALERTS) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A1F33),
                    contentColor = Color.White
                )
            ) {
                Text("Alerts")
            }
        }
        LazyColumn(modifier = Modifier.padding(top = 20.dp)) {
            item {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
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
            }
        }
    )
}