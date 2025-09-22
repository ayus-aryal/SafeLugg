package com.example.safelugg.myviewmodels

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PaymentRetrofitInstance {
    private const val BASE_URL = "https://658d3a603878.ngrok-free.app" // emulator -> host, change for device/real server

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: PaymentApi by lazy { retrofit.create(PaymentApi::class.java) }
}