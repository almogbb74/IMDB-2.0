package com.example.moviereview.data.api

import com.google.gson.annotations.SerializedName


data class PlacesResponse(
    val results: List<PlaceResult>,
    val status: String
)

data class PlaceResult(
    val name: String,
    @SerializedName("vicinity")
    val address: String,
    val geometry: Geometry,
)

data class Geometry(
    val location: LocationLiteral
)

data class LocationLiteral(
    val lat: Double,
    val lng: Double
)