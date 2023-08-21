package com.example.practicalappdev.networking.api


import com.example.practicalappdev.networking.models.DirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String,
        @Query("key") apiKey: String
    ): DirectionsResponse
}