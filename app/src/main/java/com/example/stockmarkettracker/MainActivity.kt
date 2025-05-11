package com.example.stockmarkettracker

import SearchPage
import StocksDatabase
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.ComponentActivity
import com.example.stockmarkettracker.ui.theme.StockMarketTrackerTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.stockmarkettracker.data.AlertRepository
import com.example.stockmarkettracker.data.TickerRepository
import com.example.stockmarkettracker.network.NetworkStockRepository
import com.example.stockmarkettracker.ui.StockViewModel
import com.example.stockmarkettracker.ui.StockViewModelFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: StockViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        val workRequest = PeriodicWorkRequestBuilder<AlertWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AlertChecker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        val db = StocksDatabase.getDatabase(this)
        val tickerRepo = TickerRepository(db.tickerDao())
        val networkRepo = NetworkStockRepository()
        val alertRepo = AlertRepository(db.priceAlertDao())
        val factory = StockViewModelFactory(networkRepo, tickerRepo, alertRepo)

        viewModel = ViewModelProvider(this, factory)[StockViewModel::class.java]
        viewModel.refreshWatchlist()
        viewModel.refreshAlerts(this)
        createNotificationChannel(this)

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

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Stock Alerts"
        val descriptionText = "Notifications for stock price alerts"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("alerts_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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