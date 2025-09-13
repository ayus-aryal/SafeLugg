package com.example.safelugg.model

data class CheckUserResponse(
    val exists: Boolean,
    val message: String,
    val id: Long? = null,
    val email: String? = null // ✅ add this

)
