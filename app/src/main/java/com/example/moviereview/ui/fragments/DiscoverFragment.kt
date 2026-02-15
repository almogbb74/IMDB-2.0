package com.example.moviereview.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviereview.R
import com.example.moviereview.databinding.FragmentDiscoverBinding
import com.example.moviereview.ui.adapters.SearchMovieAdapter
import com.example.moviereview.ui.adapters.TrendingActorAdapter
import com.example.moviereview.ui.adapters.TrendingMovieAdapter
import com.example.moviereview.utils.showSnackbar
import com.example.moviereview.viewmodels.DiscoverViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var movieAdapter: TrendingMovieAdapter
    private lateinit var actorAdapter: TrendingActorAdapter
    private lateinit var searchAdapter: SearchMovieAdapter


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                navigateToCinemas()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                navigateToCinemas()
            }

            else -> {
                // No location access granted.
                binding.root.showSnackbar(getString(R.string.location_denied))
            }
        }
    }

    private fun onFindCinemasClicked() {
        if (!isNetworkAvailable()) {
            binding.root.showSnackbar(getString(R.string.no_internet))
            return
        }
        val fineLoc = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLoc = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLoc == PackageManager.PERMISSION_GRANTED || coarseLoc == PackageManager.PERMISSION_GRANTED) {
            // Already have permission, Jump to the next step
            navigateToCinemas()
        } else {
            // Need to ask the user
            checkLocationPermissions()
        }
    }


    // The function to trigger the permission dialog
    private fun checkLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    private fun navigateToCinemas() {
        val action = DiscoverFragmentDirections.actionDiscoverFragmentToCinemasFragment()
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFindCinemas.setOnClickListener { onFindCinemasClicked() }
        initAdapters()
        setupRecyclerViews()
        setupSearchView()
        observeViewModel()
    }

    private fun initAdapters() {
        movieAdapter = TrendingMovieAdapter { }
        actorAdapter = TrendingActorAdapter { }
        searchAdapter = SearchMovieAdapter { }
    }

    private fun setupRecyclerViews() {
        binding.rvTrendingMovies.apply {
            adapter = movieAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvTrendingActors.apply {
            adapter = actorAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvSearchResults.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun observeViewModel() {
        // Observe Trending Movies
        viewModel.trendingMovies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies)
        }

        // Observe Trending Actors
        viewModel.trendingActors.observe(viewLifecycleOwner) { actors ->
            actorAdapter.submitList(actors)
        }

        // Observe Search Results
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.submitList(results)
        }

        // Observe Loading State for ProgressBar
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            if (loading) {
                binding.dashboardLayout.visibility = View.INVISIBLE
            } else {
                if (binding.searchView.query.isNullOrEmpty()) {
                    binding.dashboardLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchMovies(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Back to Dashboard mode
                    binding.dashboardLayout.visibility = View.VISIBLE
                    binding.rvSearchResults.visibility = View.GONE
                } else {
                    // Switch to Search mode
                    binding.dashboardLayout.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.VISIBLE
                    viewModel.searchMovies(newText)
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}