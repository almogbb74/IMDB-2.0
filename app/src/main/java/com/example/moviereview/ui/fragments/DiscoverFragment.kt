package com.example.moviereview.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviereview.databinding.FragmentDiscoverBinding
import com.example.moviereview.ui.adapters.SearchMovieAdapter
import com.example.moviereview.ui.adapters.TrendingActorAdapter
import com.example.moviereview.ui.adapters.TrendingMovieAdapter
import com.example.moviereview.viewmodels.DiscoverViewModel

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var movieAdapter: TrendingMovieAdapter
    private lateinit var actorAdapter: TrendingActorAdapter
    private lateinit var searchAdapter: SearchMovieAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        setupRecyclerViews()
        setupSearchView()
        observeViewModel()
    }

    private fun initAdapters() {
        movieAdapter = TrendingMovieAdapter { /* Will handle later */ }
        actorAdapter = TrendingActorAdapter { /* Will handle later */ }
        searchAdapter = SearchMovieAdapter { /* Will handle later */ }
    }

    private fun setupRecyclerViews() {
        binding.rvTrendingMovies.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvTrendingActors.apply {
            adapter = actorAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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