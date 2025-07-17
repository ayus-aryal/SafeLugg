package com.example.safelugg.myviewmodels

data class SearchRequest(
    val city: String,
    val date: String,
    val noOfBags: Int
)


data class VendorResponse(
    val vendorId: Long,
    val businessName: String,
    val city: String,
    val availableCapacity: Int,
    val pricePerBag: Int
)
