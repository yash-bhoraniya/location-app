package com.example.practicalappdev.networking.repository

import com.example.practicalappdev.networking.api.ApiServices
import com.example.practicalappdev.networking.models.DirectionsResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.practicalappdev.networking.api.Result

class DirectionsRepository(private val apiService: ApiServices) {

    suspend fun getDirections(
        origin: String,
        destination: String,
        waypoints: String,
        apiKey: String
    ): Flow<Result<DirectionsResponse>> = flow {
        emit(Result.loading())
        try {
            val directionsResponse = apiService.getDirections(origin, destination, waypoints, apiKey)
            emit(Result.success(directionsResponse))
        } catch (e: Exception) {
            emit(Result.error(e))
        }
    }
}
