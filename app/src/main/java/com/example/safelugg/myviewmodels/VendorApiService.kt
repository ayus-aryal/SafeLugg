package com.example.safelugg.myviewmodels

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VendorApiService {
    @GET("/api/partner/{vendorId}")
    suspend fun getVendorDetails(@Path("vendorId") vendorId: Long): Response<VendorFullDetailsResponse>
}
