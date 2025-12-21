package com.example.moviereview.repository

import androidx.lifecycle.LiveData
import com.example.moviereview.data.Movie
import com.example.moviereview.data.MovieDao

class MovieRepository(private val movieDao: MovieDao) {
    // Suspend functions are running on a background thread and keeping the app smooth

    // Get all movies (we just pass the LiveData through)
    val allMovies: LiveData<List<Movie>> = movieDao.getAllMovies()

    // Get favorites
    val favoriteMovies: LiveData<List<Movie>> = movieDao.getFavorites()

    // Get specific movie
    fun getMovie(id: Int): LiveData<Movie> {
        return movieDao.getMovie(id)
    }

    // Insert (Must be suspend)
    suspend fun insert(movie: Movie) {
        movieDao.insertMovie(movie)
    }

    // Update (Must be suspend)
    suspend fun update(movie: Movie) {
        movieDao.updateMovie(movie)
    }

    // Delete (Must be suspend)
    suspend fun delete(movie: Movie) {
        movieDao.deleteMovie(movie)
    }
}