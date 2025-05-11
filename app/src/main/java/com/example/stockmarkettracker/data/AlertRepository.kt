package com.example.stockmarkettracker.data

import kotlinx.coroutines.flow.Flow

class AlertRepository(private val dao: PriceAlertDao) {
    fun getAlerts(): Flow<List<PriceAlert>> = dao.getAllAlerts()
    suspend fun addAlert(alert: PriceAlert) = dao.insert(alert)
    suspend fun removeAlert(alert: PriceAlert) = dao.delete(alert)
}
