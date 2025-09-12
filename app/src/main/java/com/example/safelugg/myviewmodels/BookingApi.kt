package com.example.safelugg.myviewmodels

import com.example.safelugg.model.BookingCreateRequest
import com.example.safelugg.model.BookingResponse
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.POST

interface BookingApi {
    @POST("/api/v1/bookings")
    suspend fun createBooking(
        @Query("userId") userId: Long,
        @Body request: BookingCreateRequest
    ): BookingResponse
}