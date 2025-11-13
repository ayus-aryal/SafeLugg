package com.example.safelugg.model

import java.time.OffsetDateTime


data class BookingCreateRequest(
    val vendorId: Long,
    val bookingDate: String,
    val scheduledStartTime: String,
    val scheduledEndTime: String,
    val noOfBags: Int,
    val expectedHours: Int
)

data class BookingResponse(
    val bookingId: Long,
    val vendorId: Long,
    val userId: Long,
    val bookingDate: String,
    val scheduledStartTime: String,
    val scheduledEndTime: String,
    val noOfBags: Int,
    val expectedHours: Int,
    val status: String,
    val totalAmount: Double,
    val amountPaid: Double,
    val remainingAmount: Double,

    val razorpayOrderId: String?,
    val razorpayKeyId: String?
)

data class BookingVerificationDTO(
    val bookingId: String,
    val businessName: String,
    val address: String,
    val phoneNumber: String,
    val checkIn: OffsetDateTime?,
    val checkOut: OffsetDateTime?,
    val durationHours: Int?,
    val noOfBags: Int,
    val paidAmount: Int,
    val paymentMode: String,
    val paymentId: String,
    val paymentStatus: String
)
