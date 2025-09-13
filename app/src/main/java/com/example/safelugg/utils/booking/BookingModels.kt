package com.example.safelugg.utils.booking


import java.time.LocalTime

data class VendorOperatingHours(
    val is24x7: Boolean,
    val openTime: LocalTime? = null,
    val closeTime: LocalTime? = null,
    val openDays: Set<String> = setOf()
)

data class TimeSlot(
    val time: LocalTime,
    val isAvailable: Boolean,
    val reason: String = ""
)

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
)