package com.example.moviereview.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.moviereview.BuildConfig
import com.example.moviereview.R
import com.example.moviereview.data.api.PlaceResult
import com.example.moviereview.databinding.FragmentCinemasBinding
import com.example.moviereview.ui.adapters.CinemaAdapter
import com.example.moviereview.utils.showSnackbar
import com.example.moviereview.viewmodels.CinemasViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CinemasFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCinemasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CinemasViewModel by viewModels()

    private val cinemaAdapter = CinemaAdapter()
    private lateinit var googleMap: GoogleMap

    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the client
        fusedLocationClient =
            com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(
                requireActivity()
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCinemasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCinemas.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = cinemaAdapter
        }
        setupObservers()
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true

            // Fetch real GPS
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    viewModel.userLocation = userLatLng
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))
                    viewModel.fetchNearbyCinemas("${it.latitude},${it.longitude}", BuildConfig.GOOGLE_MAPS_API_KEY)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        // Listen for the cinema data
        viewModel.cinemas.observe(viewLifecycleOwner) { cinemaList ->
            // Update your RecyclerView adapter here
            cinemaAdapter.setData(cinemaList, viewModel.userLocation)

            // Add markers to the map
            addCinemaMarkers(cinemaList)
        }

        // Show/Hide a loading spinner
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoadingCinemas.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                android.util.Log.e("CinemaDebug", it)
                binding.root.showSnackbar(it) // Or use a Toast to see the error on screen
            }
        }
    }

    private fun addCinemaMarkers(cinemas: List<PlaceResult>) {
        // Check if the property has been initialized to prevent rotation crashes
        if (::googleMap.isInitialized) {
            googleMap.clear()
            cinemas.forEach { cinema ->
                val position = LatLng(cinema.geometry.location.lat, cinema.geometry.location.lng)
                googleMap.addMarker(
                    com.google.android.gms.maps.model.MarkerOptions()
                        .position(position)
                        .title(cinema.name)
                        .snippet(cinema.address)
                )
            }
        }
    }
}


