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
    val pricePerBag: Int,
    val imageUrls: List<String>
)


// VendorFullDetailsResponse.kt
data class VendorFullDetailsResponse(
    val vendorID: Long,
    val personalDetails: PersonalDetailsDto,
    val locationDetails: LocationDetailsDto,
    val storageDetails: StorageDetailsDto,
    val pricingDetails: PricingDetailsDto,
    val imageUrls: List<String>
)

data class PersonalDetailsDto(
    val businessName: String,
    val ownerName: String,
    val phoneNumber: String,
    val email: String
)

data class LocationDetailsDto(
    val country: String,
    val state: String,
    val city: String,
    val postalCode: String,
    val streetAddress: String,
    val landmark: String,
    val locationText: String
)

data class StorageDetailsDto(
    val capacity: Int,
    val storageTypes: String,
    val luggageSizes: Set<String>,
    val hasCCTV: Boolean,
    val hasStaff: Boolean,
    val hasLocks: Boolean,
    val securityNotes: String,
    val openDays: Set<String>,
    val openingTime: String,
    val closingTime: String,
    val is24x7: Boolean
)

data class PricingDetailsDto(
    val pricePerBag: Double,
    val note: String
)
