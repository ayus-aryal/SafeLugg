package com.example.safelugg.myviewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelugg.model.BookingCreateRequest
import com.example.safelugg.model.BookingResponse
import com.example.safelugg.model.BookingVerificationDTO
import com.example.safelugg.myviewmodels.ProvideBookingApi.bookingApi
import kotlinx.coroutines.launch

class BookingViewModel: ViewModel() {

    // --- Existing booking creation states ---
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _bookingResponse = mutableStateOf<BookingResponse?>(null)
    val bookingResponse: State<BookingResponse?> = _bookingResponse

    fun createBooking(
        userId: Long,
        request: BookingCreateRequest,
        onSuccess: (BookingResponse) -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            _isLoading.value = true
            try{
                val response = bookingApi.createBooking(userId, request)
                _bookingResponse.value = response
                onSuccess(response)
            } catch (e: Exception){
                val error = e.localizedMessage ?: "Unknown Error"
                _errorMessage.value = error
                onError(error)
            }finally{
                _isLoading.value = false
            }
        }
    }

    // --- New Booking Verification State ---
    private val _bookingVerification = mutableStateOf<BookingVerificationDTO?>(null)
    val bookingVerification: State<BookingVerificationDTO?> = _bookingVerification

    fun fetchBookingVerification(bookingId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val details = bookingApi.getBookingVerification(bookingId)
                _bookingVerification.value = details
            } catch (e: Exception) {
                val error = e.localizedMessage ?: "Failed to fetch booking verification"
                _errorMessage.value = error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
