import com.example.safelugg.myviewmodels.SearchRequest
import com.example.safelugg.myviewmodels.VendorResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomerApiService {

    @POST("api/customers/search")
    suspend fun searchVendors(@Body searchRequest: SearchRequest): Response<List<VendorResponse>>


}
