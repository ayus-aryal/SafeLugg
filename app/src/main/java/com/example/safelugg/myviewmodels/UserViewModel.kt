package com.example.safelugg.myviewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelugg.model.UserRequest
import kotlinx.coroutines.launch
import androidx.compose.runtime.*


class UserViewModel : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _success = mutableStateOf(false)
    val success: State<Boolean> = _success

    fun createUser(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userRequest = UserRequest(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phoneNumber = phoneNumber
                )
                val response = UserRetrofitInstance.api.createUser(userRequest)
                if (response.isSuccessful) {
                    Log.d("UserViewModel", "User creation successful")

                    _success.value = true
                    onSuccess()
                } else {
                    val error = "Server Error: ${response.code()}"
                    Log.e("UserViewModel", error)

                    _errorMessage.value = error
                    onError(error)
                }
            } catch (e: Exception) {
                val error = "Exception: ${e.localizedMessage}"
                Log.e("UserViewModel", error)

                _errorMessage.value = error
                onError(error)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
