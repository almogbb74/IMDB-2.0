package com.example.moviereview.repositories

import androidx.lifecycle.LiveData
import com.example.moviereview.data.api.RemoteActor
import com.example.moviereview.data.api.RemoteMovie
import com.example.moviereview.data.api.TMDbService
import com.example.moviereview.data.local.Movie
import com.example.moviereview.data.local.MovieDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val tmdbService: TMDbService
)
{
    // Get all movies
    val allMovies: LiveData<List<Movie>> = movieDao.getAllMovies()

    // Get favorites
    val favoriteMovies: LiveData<List<Movie>> = movieDao.getFavorites()

    // Get specific movie
    fun getMovie(id: Int): LiveData<Movie> {
        return movieDao.getMovie(id)
    }

    // Insert
    suspend fun insert(movie: Movie) {
        movieDao.insertMovie(movie)
    }

    // Update
    suspend fun update(movie: Movie) {
        movieDao.updateMovie(movie)
    }

    // Delete
    suspend fun delete(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    suspend fun getPopularMovies(apiKey: String): List<RemoteMovie>? {
        val response = tmdbService.getPopularMovies(apiKey)
        return if (response.isSuccessful) response.body()?.results else null
    }

    suspend fun getPopularActors(apiKey: String): List<RemoteActor>? {
        val response = tmdbService.getPopularActors(apiKey)
        return if (response.isSuccessful) response.body()?.results else null
    }

    suspend fun searchMovies(apiKey: String, query: String): List<RemoteMovie>? {
        val response = tmdbService.searchMovies(apiKey, query)
        return if (response.isSuccessful) response.body()?.results else null
    }
}
