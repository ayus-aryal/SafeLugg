package com.example.safelugg.myviewmodels

import OffsetDateTimeAdapter
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.OffsetDateTime

@RequiresApi(Build.VERSION_CODES.O)
object ProvideBookingApi {
    private const val BASE_URL = "https://a8343dd19d8a.ngrok-free.app"

    @RequiresApi(Build.VERSION_CODES.O)
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
        .create()

    val bookingApi: BookingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BookingApi::class.java)
    }
}
