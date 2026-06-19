package com.praktikum.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.praktikum.playlistmaker.search.domain.model.Track

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
        val diffCallback = TrackDiffCallback(tracks, newTracks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        tracks = newTracks
        diffResult.dispatchUpdatesTo(this)
    }

    private class TrackDiffCallback(
        private val oldList: List<Track>,
        private val newList: List<Track>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int,
        ): Boolean = oldList[oldItemPosition].trackId == newList[newItemPosition].trackId

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int,
        ): Boolean = oldList[oldItemPosition] == newList[newItemPosition]
    }
}
