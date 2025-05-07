package com.example.stockmarkettracker

import TickerItem
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.stockmarkettracker.ui.theme.StockMarketTrackerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import androidx.compose.ui.platform.LocalContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.example.stockmarkettracker.ui.StockViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.channels.ticker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            StockMarketTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TickerCard(tickerItem: TickerItem) {
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
                text = tickerItem.ticker,
                color = Color(200, 200, 200),
                fontSize = 24.sp
            )
            Text(
                text = tickerItem.name,
                color = Color.Gray,
                fontSize = 16.sp
            )
            Text(
                text = "Market: ${tickerItem.market} | Exchange: ${tickerItem.primaryExchange}",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel: StockViewModel = viewModel()
    val tickerList by viewModel.tickerList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchText = remember { mutableStateOf("") }

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

        Column(modifier = Modifier.padding(top = 20.dp)) {
            results.forEach { result ->
                Text(
                    text = "${result.ticker} – ${result.name}",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
        LazyColumn(modifier = Modifier.padding(top = 20.dp)) {
            item {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(200, 200, 200),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            items(tickerList.toList()) { result ->
                TickerCard(tickerItem = result)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPagePreview() {
    val context = LocalContext.current
    val testNavController = remember { TestNavHostController(context) }

    StockMarketTrackerTheme {
        SearchPage(navController = testNavController)
    }
}

    @Preview(showBackground = true)
    @Composable
    fun WatchlistTestPage() {
        val context = LocalContext.current
        val testNavController = remember { TestNavHostController(context) }

        StockMarketTrackerTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                WatchlistScreen(
                    navController = testNavController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
