package com.example.safelugg.myviewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class VendorViewModel : ViewModel() {

    private val _vendorDetails = mutableStateOf<VendorFullDetailsResponse?>(null)
    val vendorDetails: State<VendorFullDetailsResponse?> = _vendorDetails

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun fetchVendorDetails(vendorId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = VendorRetrofitInstance.api.getVendorDetails(vendorId)
                if (response.isSuccessful) {
                    _vendorDetails.value = response.body()
                } else {
                    _errorMessage.value = "Server Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: IOException) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
            } catch (e: HttpException) {
                _errorMessage.value = "HTTP exception: ${e.localizedMessage}"
            } catch (e: Exception) {
                _errorMessage.value = "Unknown error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
