package com.example.safelugg.myviewmodels

import com.example.safelugg.model.CheckUserResponse
import com.example.safelugg.model.UserRequest
import com.example.safelugg.model.UserResponse
import retrofit2.Response
import retrofit2.http.*


interface UserApiService {

    @GET("api/users/check-email")
    suspend fun checkUserExists(@Query("email") email: String): Response<CheckUserResponse>

    @GET("api/users/by-email")
    suspend fun getUserByEmail(@Query("email") email: String): Response<UserResponse>

    @POST("api/users/create")
    suspend fun createUser(@Body request: UserRequest): Response<UserResponse>


    @GET("api/users/all")
    suspend fun getAllUsers(): Response<List<UserResponse>>
}
