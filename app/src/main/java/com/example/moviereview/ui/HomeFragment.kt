package com.example.moviereview.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereview.utils.MovieApplication
import com.example.moviereview.R
import com.example.moviereview.data.Movie
import com.example.moviereview.databinding.FragmentHomeBinding
import com.example.moviereview.ui.adapters.MovieAdapter // Importing your new Adapter
import com.example.moviereview.utils.showSnackbar
import com.example.moviereview.viewmodel.MovieViewModel
import com.example.moviereview.viewmodel.MovieViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Initialize ViewModel using the Factory
    private val viewModel: MovieViewModel by viewModels {
        MovieViewModelFactory((requireActivity().application as MovieApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Adapter
        val movieAdapter = MovieAdapter(
            onMovieClick = { movie ->
                // Navigate to Details (Passing the ID)
                val action =
                    HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(movieId = movie.id)
                findNavController().navigate(action)
            },
            onEditClick = { movie ->
                // Navigate to Add/Edit (Passing the ID to edit)
                val action =
                    HomeFragmentDirections.actionHomeFragmentToAddEditMovieFragment(movieId = movie.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { movie ->
                // Toggle Favorite status in Database
                val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)
                viewModel.update(updatedMovie)

                val msg =
                    if (updatedMovie.isFavorite) getString(R.string.added_to_favorites) else getString(
                        R.string.removed_from_favorites
                    )
                binding.root.showSnackbar(msg)
            }
        )

        // Setup RecyclerView
        binding.homeRecyclerView.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        // Attach Swipe-to-Delete
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // We don't support drag-and-drop moving, only swiping
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Get the movie that was swiped
                val position = viewHolder.adapterPosition
                val movie = movieAdapter.currentList[position]

                // Show Confirmation Dialog
                showDeleteConfirmationDialog(movie, position, movieAdapter)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.homeRecyclerView)

        // Observe Database Data
        viewModel.allMovies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.submitList(movies)
        }
    }

    private fun showDeleteConfirmationDialog(movie: Movie, position: Int, adapter: MovieAdapter) {
        // Inflate the Custom Layout
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_movie, null)

        // Build the Alert Dialog using the custom view
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true) // Allow dismissing on back press

        val dialog = builder.create()

        // Make the background transparent so our rounded CardView shows correctly
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Bind the Data (Title and Message)
        val tvMessage = dialogView.findViewById<android.widget.TextView>(R.id.tv_dialog_message)
        tvMessage.text = getString(R.string.dialog_confirm, movie.title)

        // Handle Button Clicks
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel)
        val btnDelete = dialogView.findViewById<View>(R.id.btn_delete)

        btnCancel.setOnClickListener {
            // User cancelled: We must restore the item in the list because it was swiped away
            adapter.notifyItemChanged(position)
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            // User confirmed: Delete from DB
            viewModel.delete(movie)
            binding.root.showSnackbar(getString(R.string.movie_deleted))
            dialog.dismiss()
        }

        // Handle "Back Press" or "Click Outside" dismissal
        dialog.setOnCancelListener {
            // If they click outside or press back, we ALSO need to restore the item
            adapter.notifyItemChanged(position)
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}