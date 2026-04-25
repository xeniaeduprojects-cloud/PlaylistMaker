package com.praktikum.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.playlistmaker.search.data.model.Track

class TrackAdapter(
    private val onTrackClick: (Track) -> Unit = {},
) : RecyclerView.Adapter<TrackViewHolder>() {
    private var tracks: List<Track> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int,
    ) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClick(track)
        }
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        // NB: google says to use diffutil, but it requires ids,
        // which are not present in the model now. And fake data is small enough.
        notifyDataSetChanged()
    }
}
