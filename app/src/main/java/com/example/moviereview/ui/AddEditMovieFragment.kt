package com.example.moviereview.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.moviereview.R
import com.example.moviereview.data.Movie
import com.example.moviereview.databinding.FragmentAddEditMovieBinding
import com.example.moviereview.utils.showSnackbar
import com.example.moviereview.viewmodel.MovieViewModel
import com.example.moviereview.viewmodel.MovieViewModelFactory

class AddEditMovieFragment : Fragment() {

    private var _binding: FragmentAddEditMovieBinding? = null
    private val binding get() = _binding!!

    // Get arguments passed from HomeFragment (contains the movieId)
    private val args: AddEditMovieFragmentArgs by navArgs()

    // Initialize ViewModel
    private val viewModel: MovieViewModel by viewModels {
        MovieViewModelFactory(requireContext())
    }


    // Variable to hold the selected image URI
    private var selectedImageUri: String? = null
    private var isCurrentMovieFavorite: Boolean = false

    // Image Picker Setup
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Crucial: We need to ask for permanent permission to read this file
                // otherwise the image will disappear when the app restarts.
                try {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    requireContext().contentResolver.takePersistableUriPermission(it, flag)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Save the URI string and show it in the ImageView
                selectedImageUri = it.toString()
                binding.ivMoviePosterPreview.imageTintList = null
                Glide.with(this)
                    .load(it)
                    .centerCrop()
                    .into(binding.ivMoviePosterPreview)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = args.movieId

        // CHECK MODE: Are we Adding (-1) or Editing (=/ -1)?
        if (movieId != -1) {
            // EDIT MODE: Change title and load data
            binding.tvScreenTitle.text = getString(R.string.edit_movie)
            viewModel.getMovie(movieId).observe(viewLifecycleOwner) { movie ->
                // Guard clause: If movie is null (deleted), stop
                if (movie == null) return@observe

                // Pre-fill fields
                binding.etTitle.setText(movie.title)
                binding.etDescription.setText(movie.description)
                binding.etReview.setText(movie.reviewText)
                binding.ratingBar.rating = movie.score
                selectedImageUri = movie.imageUri
                isCurrentMovieFavorite = movie.isFavorite

                // Load existing image
                if (!selectedImageUri.isNullOrEmpty()) {
                    binding.ivMoviePosterPreview.imageTintList = null
                    Glide.with(this).load(selectedImageUri).centerCrop()
                        .into(binding.ivMoviePosterPreview)
                }
            }
        }

        // Handle Image Click -> Open Gallery
        binding.ivMoviePosterPreview.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Handle Save Button
        binding.btnSaveMovie.setOnClickListener {
            saveMovie(movieId)
        }
    }

    private fun saveMovie(currentId: Int) {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val review = binding.etReview.text.toString().trim()
        val score = binding.ratingBar.rating

        // Validation
        if (title.isEmpty()) {
            binding.etTitle.error = getString(R.string.title_required)
            return
        }

        // Create the Movie Object
        // If currentId is -1 (New), we let Room auto-generate the ID (pass 0)
        // If currentId is not -1 (Edit), we reuse it to update the existing row
        val movie = Movie(
            id = if (currentId == -1) 0 else currentId,
            title = title,
            description = description,
            reviewText = review,
            score = score,
            imageUri = selectedImageUri,
            isFavorite = isCurrentMovieFavorite
        )

        // Save to Database
        if (currentId == -1) {
            viewModel.insert(movie)
            binding.root.showSnackbar(getString(R.string.movie_added))

        } else {
            viewModel.update(movie)
            binding.root.showSnackbar(getString(R.string.movie_edited))
        }

        // Navigate Back
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}