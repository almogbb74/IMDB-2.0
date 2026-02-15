package com.example.moviereview.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviereview.data.api.PlaceResult
import com.example.moviereview.repositories.PlacesRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CinemasViewModel @Inject constructor(
    private val repository: PlacesRepository
) : ViewModel() {

    var userLocation: LatLng? = null
    private val _cinemas = MutableLiveData<List<PlaceResult>>()
    val cinemas: LiveData<List<PlaceResult>> get() = _cinemas

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchNearbyCinemas(location: String, apiKey: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getNearbyCinemas(location, apiKey)

                if (response.status == "OK") {
                    _cinemas.value = response.results
                } else {
                    _error.value = "API Error: ${response.status}"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}