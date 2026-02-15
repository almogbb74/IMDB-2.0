package com.example.moviereview.repositories


import com.example.moviereview.data.api.PlacesApiService
import com.example.moviereview.data.api.PlacesResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(
    private val placesApiService: PlacesApiService
) {
    suspend fun getNearbyCinemas(location: String, apiKey: String): PlacesResponse {
        return placesApiService.getNearbyCinemas(location, apiKey = apiKey)
    }
}