package com.example.moviereview.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.moviereview.databinding.FragmentMovieDetailsBinding
import com.example.moviereview.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    // Get the arguments (movieId) passed from the Home/Favorites Fragment
    private val args: MovieDetailsFragmentArgs by navArgs()

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = args.movieId

        // Observe the specific movie data
        viewModel.getMovie(movieId).observe(viewLifecycleOwner) { movie ->
            // If movie is null (e.g., was deleted), go back to previous screen
            if (movie == null) {
                findNavController().popBackStack()
                return@observe
            }

            // Bind Data to UI
            binding.tvDetailTitle.text = movie.title

            // Rating Bar and Score Text
            binding.detailRatingBar.rating = movie.score
            binding.tvDetailScore.text = String.format(Locale.getDefault(), "%.1f/5.0", movie.score)

            // Description (The Plot)
            binding.tvDetailDescription.text = movie.description

            // Review (Your Personal Opinion)
            binding.tvDetailReview.text = movie.reviewText

            // Load Image
            if (!movie.imageUri.isNullOrEmpty()) {
                binding.ivMoviePosterLarge.imageTintList = null
                Glide.with(this)
                    .load(movie.imageUri)
                    .centerCrop()
                    .into(binding.ivMoviePosterLarge)
            }
        }

        // Handle Back Button
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}