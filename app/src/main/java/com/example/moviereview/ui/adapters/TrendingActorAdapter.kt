package com.example.moviereview.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviereview.data.api.RemoteActor
import com.example.moviereview.databinding.ItemTrendingActorBinding
import com.example.moviereview.utils.Constants

class TrendingActorAdapter(private val onActorClick: (RemoteActor) -> Unit) :
    ListAdapter<RemoteActor, TrendingActorAdapter.ActorViewHolder>(ActorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val binding = ItemTrendingActorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ActorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ActorViewHolder(private val binding: ItemTrendingActorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(actor: RemoteActor) {
            binding.tvActorName.text = actor.name

            // Construct full URL for actor profile image
            val fullImageUrl = "${Constants.IMAGE_BASE_URL}${actor.profilePath}"

            Glide.with(binding.ivActorProfile.context)
                .load(fullImageUrl)
                .placeholder(android.R.drawable.progress_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .circleCrop() // Forces circular shape even if source is square
                .into(binding.ivActorProfile)

            binding.root.setOnClickListener { onActorClick(actor) }
        }
    }

    class ActorDiffCallback : DiffUtil.ItemCallback<RemoteActor>() {
        override fun areItemsTheSame(oldItem: RemoteActor, newItem: RemoteActor) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RemoteActor, newItem: RemoteActor) = oldItem == newItem
    }
}