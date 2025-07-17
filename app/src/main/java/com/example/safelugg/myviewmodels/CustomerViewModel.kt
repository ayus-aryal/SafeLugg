package com.example.safelugg.myviewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class CustomerViewModel : ViewModel() {

    private val _searchResults = mutableStateOf<List<VendorResponse>>(emptyList())
    val searchResults: State<List<VendorResponse>> = _searchResults

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun searchVendors(city: String, date: String, noOfBags: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitInstance.api.searchVendors(SearchRequest(city, date, noOfBags))
                if (response.isSuccessful) {
                    _searchResults.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown Error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

