package com.example.moviereview.utils

import android.app.Application
import androidx.room.Room
import com.example.moviereview.data.MovieDatabase
import com.example.moviereview.repository.MovieRepository
import kotlin.getValue

class MovieApplication : Application() {

    // Create the Database instance (Lazy means it's created only when first needed)
    private val database by lazy {
        Room.databaseBuilder(
            this,
            MovieDatabase::class.java,
            "movie_database"
        )
            // .allowMainThreadQueries() // Uncomment this ONLY if you get stuck, but we are using Coroutines so we don't need it!
            .build()
    }

    // Create the Repository instance (Singleton)
    val repository by lazy {
        MovieRepository(database.movieDao())
    }
}