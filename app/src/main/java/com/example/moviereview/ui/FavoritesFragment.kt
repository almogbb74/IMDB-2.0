package com.example.moviereview.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviereview.utils.MovieApplication
import com.example.moviereview.databinding.FragmentFavoritesBinding
import com.example.moviereview.ui.adapters.MovieAdapter
import com.example.moviereview.utils.showSnackbar
import com.example.moviereview.viewmodel.MovieViewModel
import com.example.moviereview.viewmodel.MovieViewModelFactory

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    // Initialize ViewModel
    private val viewModel: MovieViewModel by viewModels {
        MovieViewModelFactory((requireActivity().application as MovieApplication).repository)
    }

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
        val movieAdapter = MovieAdapter(
            onMovieClick = { movie ->
                // Navigate to Details
                val action =
                    FavoritesFragmentDirections.actionFavoritesFragmentToMovieDetailsFragment(movie.id)
                findNavController().navigate(action)
            },
            onEditClick = { movie ->
                // Navigate to Edit
                val action =
                    FavoritesFragmentDirections.actionFavoritesFragmentToAddEditMovieFragment(movie.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { movie ->
                // Toggle Favorite status
                val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)
                viewModel.update(updatedMovie)

                // Show snackbar
                binding.root.showSnackbar("Removed from Favorites")
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