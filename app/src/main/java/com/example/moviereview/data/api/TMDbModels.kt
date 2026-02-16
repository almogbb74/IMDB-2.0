package com.example.moviereview.data.api

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("results") val results: List<RemoteMovie>
)

// The Movie Object from the API
data class RemoteMovie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val rating: Double,
    @SerializedName("release_date") val releaseDate: String?
)

// Wrapper for Actor Lists
data class ActorResponse(
    @SerializedName("results") val results: List<RemoteActor>
)

// The Actor Object from the API
data class RemoteActor(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("profile_path") val profilePath: String? // The actor's face image
)