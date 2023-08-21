package com.example.practicalappdev.networking.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RouteApi {
    val apiService: ApiServices

    init {
        val directionsApiService = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = directionsApiService.create(ApiServices::class.java)
    }

    // Rest of the RouteApi class...
}