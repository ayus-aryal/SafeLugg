package com.example.safelugg.model


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
