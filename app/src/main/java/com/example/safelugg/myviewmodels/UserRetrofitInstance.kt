package com.example.safelugg.myviewmodels

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserRetrofitInstance {
    val api: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://4e03af3df835.ngrok-free.app/") // Replace with actual IP and port
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }
}
