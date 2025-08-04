package com.example.safelugg.myviewmodels

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserRetrofitInstance {
    val api: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://b51fa8888a05.ngrok-free.app/") // Replace with actual IP and port
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }
}
