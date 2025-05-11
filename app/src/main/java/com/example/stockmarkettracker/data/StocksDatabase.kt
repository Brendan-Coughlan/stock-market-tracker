import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stockmarkettracker.data.PriceAlert
import com.example.stockmarkettracker.data.PriceAlertDao
import com.example.stockmarkettracker.data.Ticker
import com.example.stockmarkettracker.data.TickerDao

@Database(entities = [Ticker::class, PriceAlert::class], version = 1, exportSchema = false)
abstract class StocksDatabase : RoomDatabase() {
    abstract fun tickerDao(): TickerDao
    abstract fun priceAlertDao(): PriceAlertDao

    companion object{
        @Volatile
        private var Instance: StocksDatabase? = null

        fun getDatabase(context: Context): StocksDatabase {
            return Instance?: synchronized(this) {
                Room.databaseBuilder(context, StocksDatabase::class.java, "stocks_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}