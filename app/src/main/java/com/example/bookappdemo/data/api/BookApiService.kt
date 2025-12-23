import com.example.bookappdemo.data.network.NetworkBook
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {
    @GET("Books")
    suspend fun searchBooks(@Query("title") query: String): List<NetworkBook>

    @GET("Books")
    suspend fun getRecommendedBooks(): List<NetworkBook>

    @GET("Books/{id}")
    suspend fun getBookById(@Path("id") id: String): NetworkBook

    @POST("Books")
    suspend fun addBook(@Body book: NetworkBook): NetworkBook

    @PUT("Books/{id}")
    suspend fun updateBook(@Path("id") id: String, @Body book: NetworkBook): NetworkBook

    @DELETE("Books/{id}")
    suspend fun deleteBook(@Path("id") id: String)

}