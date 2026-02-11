package com.example.moviereview.viewmodels


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.moviereview.data.local.Movie
import com.example.moviereview.repository.MovieRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository = MovieRepository(application)

    val allMovies: LiveData<List<Movie>> = repository.allMovies
    val favoriteMovies: LiveData<List<Movie>> = repository.favoriteMovies

    fun getMovie(id: Int): LiveData<Movie> {
        return repository.getMovie(id)
    }

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