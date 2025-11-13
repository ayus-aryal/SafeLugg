package com.example.safelugg.myviewmodels

import com.example.safelugg.model.BookingCreateRequest
import com.example.safelugg.model.BookingResponse
import com.example.safelugg.model.BookingVerificationDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingApi {
    @POST("/api/v1/bookings")
    suspend fun createBooking(
        @Query("userId") userId: Long,
        @Body request: BookingCreateRequest
    ): BookingResponse



    @GET("/api/bookings/{bookingId}/verification")
    suspend fun getBookingVerification(@Path("bookingId") bookingId: Long): BookingVerificationDTO
}