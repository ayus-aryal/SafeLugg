package com.example.safelugg.model

data class UserResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val createdAt: String,
    val updatedAt: String
)
