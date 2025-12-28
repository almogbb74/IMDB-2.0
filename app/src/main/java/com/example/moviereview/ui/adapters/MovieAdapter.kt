package com.example.moviereview.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviereview.R
import com.example.moviereview.data.Movie
import com.example.moviereview.databinding.ItemMovieBinding

class MovieAdapter(
    private val onMovieClick: (Movie) -> Unit,
    private val onEditClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        // This inflates the layout item_movie.xml
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            // Set Title
            binding.tvMovieTitle.text = movie.title

            // Set Rating (format: "9.0")
            binding.tvMovieRating.text = movie.score.toString()

            // Load Image using Glide
            if (!movie.imageUri.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(movie.imageUri)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery) // Fallback while loading
                    .into(binding.ivMoviePoster)
            } else {
                // If no image, show a default grey background or icon
                binding.ivMoviePoster.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // Handle Heart Icon (Red for Favorite, Grey for not)
            binding.ivFavorite.setImageResource(R.drawable.ic_heart)

            if (movie.isFavorite) {
                // User loves this movie -> RED
                binding.ivFavorite.setColorFilter(Color.RED)
            } else {
                // Standard state -> GREY
                binding.ivFavorite.setColorFilter(Color.GRAY)
            }

            // Click Listeners

            // Click on the whole card -> Details
            binding.root.setOnClickListener {
                onMovieClick(movie)
            }

            // Click on Pencil -> Edit
            binding.ivEditMovie.setOnClickListener {
                onEditClick(movie)
            }

            // Click on Heart -> Toggle Favorite
            binding.ivFavorite.setOnClickListener {
                onFavoriteClick(movie)
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }
}