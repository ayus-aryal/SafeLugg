import retrofit2.Response
import retrofit2.http.GET

interface CustomerApiService {

    @GET("api/customers/test")
    suspend fun testConnection(): Response<String>

    @GET("api/customers/nearby-lockers")
    suspend fun getNearbyLockers(): Response<List<String>>

}
