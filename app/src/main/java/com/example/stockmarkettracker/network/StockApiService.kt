import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class TickerResponse(
    val results: List<TickerItem>,
    val status: String,
    val requestId: String,
    val count: Int
)

data class TickerItem(
    val ticker: String,
    val name: String,
    val market: String,
    val locale: String,
    val primaryExchange: String,
    val type: String,
    val active: Boolean,
    val currencyName: String,
    val cik: String? = null,
    val compositeFigi: String,
    val shareClassFigi: String,
    val lastUpdatedUtc: String
)


private const val BASE_URL = "https://api.polygon.io/v3/"
private val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build()

interface StockApiService {
    @GET("reference/tickers")
    suspend fun getTickers(
        @Query("search") search: String,
        @Query("apiKey") apiKey: String
    ): TickerResponse
}

object StockApi {
    val retrofitService : StockApiService by lazy {
        retrofit.create(StockApiService::class.java)
    }
}