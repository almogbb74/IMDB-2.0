package com.example.moviereview.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereview.data.api.PlaceResult
import com.example.moviereview.databinding.ItemCinemaBinding
import com.google.android.gms.maps.model.LatLng
import java.util.Locale
import androidx.core.net.toUri

class CinemaAdapter : RecyclerView.Adapter<CinemaAdapter.CinemaViewHolder>() {

    private var cinemas: List<PlaceResult> = emptyList()
    private var userLocation: LatLng? = null

    fun setData(newCinemas: List<PlaceResult>, location: LatLng?) {
        val diffCallback = CinemaDiffCallback(this.cinemas, newCinemas)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.cinemas = newCinemas
        this.userLocation = location
        diffResult.dispatchUpdatesTo(this) // Notify the adapter of the changes, if were any
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CinemaViewHolder {
        val binding = ItemCinemaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CinemaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CinemaViewHolder, position: Int) {
        val cinema = cinemas[position]
        holder.binding.tvCinemaName.text = cinema.name
        holder.binding.tvCinemaAddress.text = cinema.address

        // Calculate distance
        userLocation?.let {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                it.latitude, it.longitude,
                cinema.geometry.location.lat, cinema.geometry.location.lng,
                results
            )
            val distanceKm = results[0] / 1000
            holder.binding.tvDistance.text = String.format(Locale.getDefault(), "%.1f km", distanceKm)
        }

        holder.itemView.setOnClickListener {
            val gmmIntentUri =
                "google.navigation:q=${cinema.geometry.location.lat},${cinema.geometry.location.lng}".toUri()
            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            holder.itemView.context.startActivity(mapIntent)
        }
    }

    class CinemaDiffCallback(
        private val oldList: List<PlaceResult>,
        private val newList: List<PlaceResult>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
            // Use a unique identifier if the API provides one, otherwise names/coordinates
            return oldList[oldPos].name == newList[newPos].name
        }

        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos] == newList[newPos]
        }
    }

    override fun getItemCount() = cinemas.size

    class CinemaViewHolder(val binding: ItemCinemaBinding) : RecyclerView.ViewHolder(binding.root)
}