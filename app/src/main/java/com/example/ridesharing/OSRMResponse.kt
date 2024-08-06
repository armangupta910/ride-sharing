package com.example.ridesharing

data class OSRMResponse(
    val routes: List<Route>
)

data class Route(
    val geometry: String,
    val distance: Double
)
