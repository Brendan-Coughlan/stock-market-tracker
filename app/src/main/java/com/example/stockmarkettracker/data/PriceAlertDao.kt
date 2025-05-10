package com.example.stockmarkettracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alert")
    fun getAllAlerts(): Flow<List<PriceAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: PriceAlert)

    @Delete
    suspend fun delete(alert: PriceAlert)
}
