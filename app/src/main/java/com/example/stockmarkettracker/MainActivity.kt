package com.example.stockmarkettracker

import TickerItem
import TickerResponse
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.compose.material3.Button
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

fun performSearch(query: String, results: SnapshotStateList<TickerItem>, coroutineScope : CoroutineScope) {
    results.clear()
    coroutineScope.launch {
        try {
            val response = StockApi.retrofitService.getTickers(
                search = query,
                apiKey = "HpgP9ZvVg92ynx9g5xThMY3YrH3ZYP1b"
            )
            results.addAll(response.results)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Failed: ${e.message}")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController, modifier: Modifier = Modifier) {
    val searchText = remember { mutableStateOf("") }
    val results = remember { mutableStateListOf<TickerItem>() }
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier.fillMaxSize().background(Color(0, 0, 0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Search",
            color = Color(255, 255, 255),
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
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = modifier.width(250.dp)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(15.dp),
                ).background(Color.Black),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    // Call search
                    performSearch(searchText.value, results, coroutineScope)
                }
            )
        )

        Button(
            onClick = {
                performSearch(searchText.value, results, coroutineScope)
            },
            modifier = Modifier.padding(top = 12.dp)
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
