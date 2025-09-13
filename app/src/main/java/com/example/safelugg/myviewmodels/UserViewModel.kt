package com.example.safelugg.myviewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelugg.model.UserRequest
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.example.safelugg.model.UserResponse
import com.example.safelugg.utils.PreferenceHelper


class UserViewModel : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _success = mutableStateOf(false)
    val success: State<Boolean> = _success

    fun createUser(
        context: Context,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        onSuccess: (UserResponse) -> Unit,
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
                    val createdUser = response.body()  // ✅ this contains the UserResponse from backend
                    if (createdUser != null) {
                        Log.d("UserViewModel", "User creation successful: $createdUser")

                        // Save userId to SharedPreferences
                        PreferenceHelper.setUserId(context, createdUser.id)
                        PreferenceHelper.setUserEmail(context, createdUser.email) // NEW

                        Log.d("UserFlow", "Saved user ID: ${createdUser.id}")


                        _success.value = true
                        onSuccess(createdUser) // ✅ pass the user object back
                    } else {
                        val error = "Empty response body"
                        Log.e("UserViewModel", error)

                        _errorMessage.value = error
                        onError(error)
                    }
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

