//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import com.example.stockmarkettracker.data.Ticker
//
//@Database(entities = [Ticker::class], version = 1, exportSchema = false)
//abstract class StocksDatabase() {
//    abstract fun tickerDao(): TickerDao
//
//    companion object{
//        @Volatile
//        private var Instance: StocksDatabase? = null
//
//        fun getDatabase(context: Context): StocksDatabase {
//            return Instance?: synchronized(this) {
//                Room.databaseBuilder(context, StocksDatabase::class.java, "stocks_database")
//                    .build()
//                    .also { Instance = it }
//            }
//        }
//    }
//}