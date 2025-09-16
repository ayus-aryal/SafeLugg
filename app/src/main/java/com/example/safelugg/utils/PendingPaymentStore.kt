package com.example.safelugg.utils

import java.util.concurrent.ConcurrentHashMap

object PendingPaymentStore {
    // razorpayOrderId -> localPaymentId
    val orderToLocalPayment = ConcurrentHashMap<String, Long>()
}