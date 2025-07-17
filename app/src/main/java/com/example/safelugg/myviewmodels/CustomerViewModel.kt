package com.example.safelugg.myviewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class CustomerViewModel : ViewModel() {

    private val _apiResponse = mutableStateOf("")
    val apiResponse: State<String> = _apiResponse

    fun fetchApiResponse() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.testConnection()
                if (response.isSuccessful) {
                    _apiResponse.value = response.body() ?: "Empty Response"
                } else {
                    _apiResponse.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _apiResponse.value = "Exception: ${e.localizedMessage}"
            }
        }
    }
}

