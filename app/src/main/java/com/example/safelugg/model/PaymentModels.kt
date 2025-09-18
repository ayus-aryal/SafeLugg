package com.example.safelugg.model

import com.google.gson.annotations.SerializedName

data class PaymentInitResponse(
    @SerializedName("paymentId") val paymentId: Long,
    @SerializedName("bookingId") val bookingId: Long,
    @SerializedName("amount") val amount: Double,      // amount in rupees (e.g. 250.0)
    @SerializedName("currency") val currency: String,
    @SerializedName("status") val status: String,
    @SerializedName("razorpayOrderId") val razorpayOrderId: String
)

data class CheckoutVerifyRequest(
    @SerializedName("razorpayOrderId") val razorpayOrderId: String,
    @SerializedName("razorpayPaymentId") val razorpayPaymentId: String,
    @SerializedName("razorpaySignature") val razorpaySignature: String
)


data class PaymentResponseDTO(
    val id: Long,
    val bookingId: Long,
    val amount: java.lang.Double,
    val currency: String,
    val status: String,
//    val razorpayOrderId: String?,
//    val razorpayPaymentId: String?

    @SerializedName("razorpayOrderId") val razorpayOrderId: String,
    @SerializedName("razorpayPaymentId") val razorpayPaymentId: String
)
