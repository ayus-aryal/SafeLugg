package com.example.safelugg.myviewmodels

import CustomerApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    val api: CustomerApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://c9a2d11fa280.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CustomerApiService::class.java)
    }
}