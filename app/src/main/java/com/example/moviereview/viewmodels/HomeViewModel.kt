package com.example.moviereview.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviereview.data.local.Movie
import com.example.moviereview.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {
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