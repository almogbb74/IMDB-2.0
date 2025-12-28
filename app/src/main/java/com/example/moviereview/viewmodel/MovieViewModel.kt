package com.example.moviereview.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moviereview.data.Movie
import com.example.moviereview.data.MovieDatabase
import com.example.moviereview.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    // We will use viewModel: MovieViewModel by viewModels because although activityViewModels lifecycle
    // is the life cycle of the entire app (activity), its better for us to use viewModels for each
    // individual fragments, since we take our data directly from the ROOM and fragments wont share
    // data with each other- and for that reason we use viewModels.

    // LiveData to be observed by the Home Fragment
    val allMovies: LiveData<List<Movie>> = repository.allMovies

    // LiveData for the Favorites Fragment
    val favoriteMovies: LiveData<List<Movie>> = repository.favoriteMovies

    // Helper to get a single movie (for Edit/Details screens)
    fun getMovie(id: Int): LiveData<Movie> {
        return repository.getMovie(id)
    }

    // Operations (Launched in a background thread via viewModelScope)
    fun insert(movie: Movie) = viewModelScope.launch {
        repository.insert(movie)
    }

    fun update(movie: Movie) = viewModelScope.launch {
        repository.update(movie)
    }

    fun delete(movie: Movie) = viewModelScope.launch {
        repository.delete(movie)
    }
}

// The Factory (we will use this to create our ViewModel instance whenever and wherever we need it.)

class MovieViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {

            // Get the Singleton Database
            val database = MovieDatabase.getDatabase(context)

            // Create the Repository here
            val repository = MovieRepository(database.movieDao())

            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}