//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.example.stockmarkettracker.data.Ticker
//
//@Dao
//interface TickerDao {
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insert(ticker: Ticker)
//
//    @Delete
//    suspend fun delete(ticker: Ticker)
//
//    @Query("Select * FROM tickers ORDER BY symbol ASC")
//    fun getAllTickers(): FlowList<Ticker>
//}