package com.example.stockmarkettracker

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stockmarkettracker.data.AlertRepository
import com.example.stockmarkettracker.data.PriceAlert
import com.example.stockmarkettracker.network.NetworkStockRepository
import kotlinx.coroutines.flow.first

class AlertWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext

        // Get database + repositories
        val db = StocksDatabase.getDatabase(appContext)
        val alertRepo = AlertRepository(db.priceAlertDao())
        val networkRepo = NetworkStockRepository()

        // Get alert list
        val alerts = alertRepo.getAlerts().first()
        val symbols = alerts.map { it.symbol }.distinct()

        if (symbols.isNotEmpty()) {
            val symbolString = symbols.joinToString(",")
            val response = StockApi.retrofitService.getSnapshotTickers(
                tickers = symbolString,
                apiKey = "HpgP9ZvVg92ynx9g5xThMY3YrH3ZYP1b"
            )

            val priceMap = response.tickers.associate { it.ticker to it.day.c }

            // Check alerts manually here (not through ViewModel)
            alerts.forEach { alert ->
                val price = priceMap[alert.symbol] ?: return@forEach
                val triggered = if (alert.isAbove) {
                    price >= alert.targetPrice
                } else {
                    price <= alert.targetPrice
                }

                if (triggered) {
                    sendNotification(appContext, alert.symbol, price, alert)
                }
            }
        }

        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(context: Context, symbol: String, price: Double, alert: PriceAlert) {
        val builder = NotificationCompat.Builder(context, "alerts_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Price Alert: $symbol")
            .setContentText("Current price $price hit your target ${if (alert.isAbove) "above" else "below"} ${alert.targetPrice}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
