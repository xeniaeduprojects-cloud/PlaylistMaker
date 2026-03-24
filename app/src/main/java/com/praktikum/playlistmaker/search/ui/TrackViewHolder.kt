package com.praktikum.playlistmaker.search.ui

import androidx.recyclerview.widget.RecyclerView
import com.praktikum.playlistmaker.databinding.ItemTrackBinding
import com.praktikum.playlistmaker.search.data.model.Track

class TrackViewHolder(
    private val binding: ItemTrackBinding,
) : RecyclerView.ViewHolder(binding.root) {
    @Suppress("ForbiddenComment")
    fun bind(track: Track) {
        // TODO: load album art
        binding.trackTitle.text = track.trackName
        binding.trackSubtitle.text = "${track.artistName} • ${track.trackTime}"
    }
}
