package com.example.stockmarkettracker

import SearchPage
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

@Preview(showBackground = true)
@Composable
fun SearchPagePreview() {
    val context = LocalContext.current
    val testNavController = remember { TestNavHostController(context) }

    StockMarketTrackerTheme {
        SearchPage(navController = testNavController)
    }
}

//        @Preview(showBackground = true)
//    @Composable
//    fun WatchlistTestPage() {
//        val context = LocalContext.current
//        val testNavController = remember { TestNavHostController(context) }
//
//        StockMarketTrackerTheme {
//            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                WatchlistScreen(
//                    navController = testNavController,
//                    modifier = Modifier.padding(innerPadding)
//                )
//            }
//        }
//    }
