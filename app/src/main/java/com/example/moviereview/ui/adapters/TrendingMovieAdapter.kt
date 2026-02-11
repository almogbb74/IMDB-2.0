package com.example.moviereview.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviereview.data.api.RemoteMovie
import com.example.moviereview.databinding.ItemTrendingMovieBinding
import com.example.moviereview.utils.Constants

class TrendingMovieAdapter(private val onMovieClick: (RemoteMovie) -> Unit) :
    ListAdapter<RemoteMovie, TrendingMovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemTrendingMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(private val binding: ItemTrendingMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: RemoteMovie) {
            binding.tvMovieTitle.text = movie.title

            // Load Image with Glide
            val fullImageUrl = "${Constants.IMAGE_BASE_URL}${movie.posterPath}"

            Glide.with(binding.ivMoviePoster.context)
                .load(fullImageUrl)
                .centerCrop()
                .into(binding.ivMoviePoster)

            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<RemoteMovie>() {
        override fun areItemsTheSame(oldItem: RemoteMovie, newItem: RemoteMovie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RemoteMovie, newItem: RemoteMovie) = oldItem == newItem
    }
}