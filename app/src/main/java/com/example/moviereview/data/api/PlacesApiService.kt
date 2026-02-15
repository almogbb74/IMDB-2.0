package com.example.moviereview.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyCinemas(
        @Query("location") location: String, // format: "lat,lng"
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String = "movie_theater",
        @Query("key") apiKey: String
    ): PlacesResponse
}