package com.example.safelugg.myviewmodels

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserRetrofitInstance {
    val api: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://29fe1d288e54.ngrok-free.app/") // Replace with actual IP and port
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }
}
