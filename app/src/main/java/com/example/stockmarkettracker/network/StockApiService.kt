import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class TickerResponse(
    val results: List<TickerItem>,
    val status: String,
    @SerializedName("request_id")
    val requestId: String,
    val count: Int
)

data class TickerItem(
    val ticker: String,
    val name: String,
    val market: String,
    val locale: String,
    @SerializedName("primary_exchange")
    val primaryExchange: String,
    val type: String,
    val active: Boolean,
    @SerializedName("currency_name")
    val currencyName: String,
    val cik: String? = null,
    @SerializedName("composite_figi")
    val compositeFigi: String,
    @SerializedName("share_class_figi")
    val shareClassFigi: String,
    @SerializedName("last_updated_utc")
    val lastUpdatedUtc: String
)


data class TickerDetailsResponse(
    val tickers: List<TickerDetailsItem>,
    val status: String,
    @SerializedName("request_id")
    val requestId: String,
    val count: Int
)

data class TickerDetailsItem(
    val ticker: String,
    val todaysChangePerc: Double,
    val todaysChange: Double,
    val updated: Long,
    val day: DayData,
    val min: MinData,
    val prevDay: DayData
)

data class DayData(
    val o: Double,
    val h: Double,
    val l: Double,
    val c: Double,
    val v: Long?,
    val vw: Double
)

data class MinData(
    val av: Long?,
    val t: Long?,
    val n: Int,
    val o: Double,
    val h: Double,
    val l: Double,
    val c: Double,
    val v: Long?,
    val vw: Double
)

private const val BASE_URL = "https://api.polygon.io/"
private val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build()

interface StockApiService {
    @GET("v3/reference/tickers")
    suspend fun getTickers(
        @Query("search") search: String,
        @Query("limit") limit: Int = 50,
        @Query("apiKey") apiKey: String
    ): TickerResponse

    @GET("v2/snapshot/stocks/tickers")
    suspend fun getSnapshotTickers(
        @Query("tickers") tickers: String,
        @Query("apiKey") apiKey: String
    ): TickerDetailsResponse
}

object StockApi {
    val retrofitService : StockApiService by lazy {
        retrofit.create(StockApiService::class.java)
    }
}