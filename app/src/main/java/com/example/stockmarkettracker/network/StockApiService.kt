import com.example.stockmarkettracker.data.StockSnapshotResponse
import com.example.stockmarkettracker.data.StockSummaryResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.polygon.io/"
private val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build()

interface StockApiService {
    @GET("v3/reference/tickers")
    suspend fun getTickers(
        @Query("search") search: String,
        @Query("limit") limit: Int = 50,
        @Query("apiKey") apiKey: String
    ): StockSummaryResponse

    @GET("v2/snapshot/locale/us/markets/stocks/tickers")
    suspend fun getSnapshotTickers(
        @Query("tickers") tickers: String,
        @Query("apiKey") apiKey: String
    ): StockSnapshotResponse
}

object StockApi {
    val retrofitService : StockApiService by lazy {
        retrofit.create(StockApiService::class.java)
    }
}