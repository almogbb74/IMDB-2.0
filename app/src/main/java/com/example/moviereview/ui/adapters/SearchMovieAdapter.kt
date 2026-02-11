package com.example.moviereview.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviereview.data.api.RemoteMovie
import com.example.moviereview.databinding.ItemSearchMovieBinding
import com.example.moviereview.utils.Constants

class SearchMovieAdapter(private val onMovieClick: (RemoteMovie) -> Unit) :
    ListAdapter<RemoteMovie, SearchMovieAdapter.SearchViewHolder>(SearchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchViewHolder(private val binding: ItemSearchMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: RemoteMovie) {
            binding.tvSearchTitle.text = movie.title

            val fullImageUrl = "${Constants.IMAGE_BASE_URL}${movie.posterPath}"

            Glide.with(binding.ivSearchPoster.context)
                .load(fullImageUrl)
                .centerCrop()
                .into(binding.ivSearchPoster)

            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }

    class SearchDiffCallback : DiffUtil.ItemCallback<RemoteMovie>() {
        override fun areItemsTheSame(oldItem: RemoteMovie, newItem: RemoteMovie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RemoteMovie, newItem: RemoteMovie) = oldItem == newItem
    }
}