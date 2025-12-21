package com.example.moviereview.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies_table")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,             // Unique ID for every movie
    val title: String,           // Movie Title
    val description: String,     // Plot summary
    val reviewText: String,      // Your personal review
    val score: Float,            // Rating (e.g., 4.5)
    val imageUri: String?,       // Path to the image on the phone (Nullable just in case)
    val isFavorite: Boolean = false // For the Favorites fragment
)