package com.example.safelugg.myviewmodels

import com.example.safelugg.model.CheckoutVerifyRequest
import com.example.safelugg.model.PaymentInitResponse
import com.example.safelugg.model.PaymentResponseDTO
import retrofit2.Response
import retrofit2.http.*
import java.lang.Double

interface PaymentApi {
    @POST("/api/payments/initiate")
    suspend fun initiatePayment(
        @Query("bookingId") bookingId: Long,
        @Query("requestedAmount") requestedAmount: Double? = null
    ): Response<PaymentInitResponse>

    @POST("/api/payments/{paymentId}/verify")
    suspend fun verifyPayment(
        @Path("paymentId") paymentId: Long,
        @Body req: CheckoutVerifyRequest
    ): Response<PaymentResponseDTO>

    // Optional dev endpoints
    @POST("/api/payments/{paymentId}/success")
    suspend fun markSuccess(
        @Path("paymentId") paymentId: Long,
        @Query("razorpayOrderId") razorpayOrderId: String?,
        @Query("razorpayPaymentId") razorpayPaymentId: String?
    ): Response<PaymentResponseDTO>
}


