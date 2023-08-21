package com.example.practicalappdev.networking.models

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(
    @SerializedName("routes") val routes: List<Route>
)

data class Route(
    @SerializedName("legs") val legs: List<Leg>
)

data class Leg(
    @SerializedName("steps") val steps: List<Step>
)

data class Step(
    @SerializedName("polyline") val polyline: Polyline
)

data class Polyline(
    @SerializedName("points") val points: String
)
