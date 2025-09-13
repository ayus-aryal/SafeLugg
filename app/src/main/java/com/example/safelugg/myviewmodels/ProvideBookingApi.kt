package com.example.safelugg.myviewmodels

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProvideBookingApi {
    private const val BASE_URL = "https://17940b43a425.ngrok-free.app" // your backend URL

    val bookingApi: BookingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BookingApi::class.java)
    }
}
