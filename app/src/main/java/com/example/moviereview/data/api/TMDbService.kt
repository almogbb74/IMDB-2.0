package com.example.moviereview.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDbService {

    // Query 1: Trending Movies
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Response<MovieResponse>

    // Query 2: Trending Actors
    @GET("person/popular")
    suspend fun getPopularActors(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Response<ActorResponse>

    // Query 3: Search Movies
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "en-US"
    ): Response<MovieResponse>
}