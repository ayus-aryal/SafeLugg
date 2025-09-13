package com.example.safelugg.utils.booking

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

object BookingUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun isVendorOpenOnDay(date: LocalDate, vendorHours: VendorOperatingHours): Boolean {
        if (vendorHours.is24x7) return true

        val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        return vendorHours.openDays.contains(dayName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateAvailableTimeSlots(
        vendorOperatingHours: VendorOperatingHours,
        selectedDate: LocalDate?,
        bookingDurationHours: Int
    ): List<TimeSlot> {
        val timeSlots = mutableListOf<TimeSlot>()

        // Check if vendor is open on selected date
        if (selectedDate != null && !isVendorOpenOnDay(selectedDate, vendorOperatingHours)) {
            return emptyList() // Return empty list if closed on this day
        }

        // If vendor is 24/7, show all hours
        if (vendorOperatingHours.is24x7) {
            for (hour in 0..23) {
                timeSlots.add(TimeSlot(LocalTime.of(hour, 0), true))
            }
            return timeSlots
        }

        // For vendors with specific hours
        val openTime = vendorOperatingHours.openTime ?: LocalTime.of(9, 0)
        val closeTime = vendorOperatingHours.closeTime ?: LocalTime.of(20, 0)

        // Generate slots from open to close time
        var currentTime = openTime
        while (currentTime.isBefore(closeTime)) {
            val endTime = currentTime.plusHours(bookingDurationHours.toLong())

            val isAvailable = endTime.isBefore(closeTime) || endTime == closeTime
            val reason = if (!isAvailable) {
                val maxHours = ChronoUnit.HOURS.between(currentTime, closeTime)
                "Max ${maxHours}h from this time"
            } else ""

            timeSlots.add(TimeSlot(currentTime, isAvailable, reason))
            currentTime = currentTime.plusHours(1)
        }

        return timeSlots
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateBookingTime(
        selectedDate: LocalDate,
        selectedTime: LocalTime,
        durationHours: Int,
        vendorOperatingHours: VendorOperatingHours
    ): ValidationResult {
        // Check if vendor is open on this day
        if (!isVendorOpenOnDay(selectedDate, vendorOperatingHours)) {
            val dayName = selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            return ValidationResult(
                false,
                "Storage is closed on $dayName. Open days: ${vendorOperatingHours.openDays.joinToString(", ")}"
            )
        }

        // If vendor is 24/7, no time restrictions
        if (vendorOperatingHours.is24x7) {
            return ValidationResult(true)
        }

        val openTime = vendorOperatingHours.openTime ?: LocalTime.of(9, 0)
        val closeTime = vendorOperatingHours.closeTime ?: LocalTime.of(20, 0)

        // Check if start time is within operating hours
        if (selectedTime.isBefore(openTime) || !selectedTime.isBefore(closeTime)) {            return ValidationResult(
                false,
                "Selected time is outside operating hours (${openTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${closeTime.format(DateTimeFormatter.ofPattern("HH:mm"))})"
            )
        }

        // Check if end time exceeds closing time
        val endTime = selectedTime.plusHours(durationHours.toLong())
        if (endTime.isAfter(closeTime)) {
            val maxHours = ChronoUnit.HOURS.between(selectedTime, closeTime)
            return ValidationResult(
                false,
                "Booking duration exceeds closing time. Maximum ${maxHours} hours available from ${selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            )
        }

        return ValidationResult(true)
    }
}