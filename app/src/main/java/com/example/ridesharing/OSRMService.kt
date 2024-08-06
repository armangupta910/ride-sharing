package com.example.ridesharing

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OSRMService {
    @GET("route/v1/driving/{startLon},{startLat};{endLon},{endLat}")
    fun getRoute(
        @Path("startLon") startLon: Double,
        @Path("startLat") startLat: Double,
        @Path("endLon") endLon: Double,
        @Path("endLat") endLat: Double,
        @Query("overview") overview: String = "false"
    ): Call<OSRMResponse>
}
