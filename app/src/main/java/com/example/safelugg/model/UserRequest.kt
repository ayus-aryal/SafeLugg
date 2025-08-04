package com.example.safelugg.model

data class UserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String
)