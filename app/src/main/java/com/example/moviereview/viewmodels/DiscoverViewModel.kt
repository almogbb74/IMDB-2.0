package com.example.moviereview.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moviereview.data.api.RemoteActor
import com.example.moviereview.data.api.RemoteMovie
import com.example.moviereview.repository.MovieRepository
import com.example.moviereview.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository(application)

    private val _trendingMovies = MutableLiveData<List<RemoteMovie>?>()
    val trendingMovies: LiveData<List<RemoteMovie>?> = _trendingMovies

    private val _trendingActors = MutableLiveData<List<RemoteActor>?>()
    val trendingActors: LiveData<List<RemoteActor>?> = _trendingActors

    private val _searchResults = MutableLiveData<List<RemoteMovie>?>()
    val searchResults: LiveData<List<RemoteMovie>?> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadTrendingData()
    }

    private fun loadTrendingData() {
        viewModelScope.launch {
            _isLoading.value = true

            val moviesDeferred = async { repository.getPopularMovies(Constants.TMDB_API_KEY) }
            val actorsDeferred = async { repository.getPopularActors(Constants.TMDB_API_KEY) }

            val movies = moviesDeferred.await()
            val actors = actorsDeferred.await()

            _trendingMovies.value = movies
            _trendingActors.value = actors

            _isLoading.value = false
        }
    }

    fun searchMovies(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = null
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val results = repository.searchMovies(Constants.TMDB_API_KEY, query)
            _searchResults.value = results
            _isLoading.value = false
        }
    }
}