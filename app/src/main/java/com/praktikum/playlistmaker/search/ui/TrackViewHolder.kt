package com.praktikum.playlistmaker.search.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.databinding.ItemTrackBinding
import com.praktikum.playlistmaker.search.data.model.Track

class TrackViewHolder(
    private val binding: ItemTrackBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(track: Track) {
        val radiusPx = itemView.context.resources.getDimensionPixelSize(R.dimen.album_art_corner_radius)
        Glide
            .with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .transform(RoundedCorners(radiusPx))
            .into(binding.trackAlbumArt)

        binding.trackTitle.text = track.trackName
        binding.trackSubtitle.text =
            itemView.context.getString(
                R.string.track_subtitle_format,
                track.artistName,
                track.trackTime,
            )
    }
}
