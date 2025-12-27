package com.example.moviereview.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {
    // Suspend functions are running on a background thread and keeping the app smooth

    // Get all movies for the Home Screen (Ordered by newest first)
    @Query("SELECT * FROM movies_table ORDER BY id DESC")
    fun getAllMovies(): LiveData<List<Movie>>

    // Get only Favorite movies for the Favorites Screen
    @Query("SELECT * FROM movies_table WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavorites(): LiveData<List<Movie>>

    // Get a specific movie (for Details/Edit screens)
    @Query("SELECT * FROM movies_table WHERE id = :id")
    fun getMovie(id: Int): LiveData<Movie>

    // Insert a new movie (suspend = runs in background)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    // Update an existing movie
    @Update
    suspend fun updateMovie(movie: Movie)

    // Delete a movie
    @Delete
    suspend fun deleteMovie(movie: Movie)
}