package com.example.moviereview.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviereview.R
import com.example.moviereview.databinding.FragmentFavoritesBinding
import com.example.moviereview.ui.adapters.LocalMovieAdapter
import com.example.moviereview.utils.showSnackbar
import com.example.moviereview.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    // Initialize ViewModel
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Adapter
        val movieAdapter = LocalMovieAdapter(
            onMovieClick = { movie ->
                // Navigate to Details
                val action =
                    FavoritesFragmentDirections.Companion.actionFavoritesFragmentToMovieDetailsFragment(movie.id)
                findNavController().navigate(action)
            },
            onEditClick = { movie ->
                // Navigate to Edit
                val action =
                    FavoritesFragmentDirections.Companion.actionFavoritesFragmentToAddEditMovieFragment(movie.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { movie ->
                // Toggle Favorite status
                val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)
                viewModel.update(updatedMovie)

                // Show snackbar
                binding.root.showSnackbar(getString(R.string.removed_from_favorites))
            }
        )

        // Setup RecyclerView
        binding.favoritesRecyclerView.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        // Observe ONLY Favorites
        viewModel.favoriteMovies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies)

            if (movies.isEmpty()) {
                binding.tvEmptyFavorites.visibility = View.VISIBLE
            } else {
                binding.tvEmptyFavorites.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}