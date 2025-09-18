package com.example.safelugg.myviewmodels

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object VendorRetrofitInstance {
    val api: VendorApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://29fe1d288e54.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VendorApiService::class.java)
    }
}
