package com.praktikum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.databinding.ItemTrackBinding
import com.praktikum.playlistmaker.search.data.model.Track

class TrackViewHolder private constructor(
    private val binding: ItemTrackBinding,
) : RecyclerView.ViewHolder(binding.root) {
    constructor(parent: ViewGroup) : this(
        ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    fun bind(track: Track) {
        val cornerRadiusPx =
            itemView.context.resources.getDimensionPixelSize(
                R.dimen.track_item_album_art_corner_radius_small,
            )

        Glide
            .with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .centerCrop()
            .fitCenter()
            .transform(RoundedCorners(cornerRadiusPx))
            .into(binding.imgAlbumArt)

        binding.tvTrackTitle.text = track.trackName
        binding.tvTrackSubtitle.text =
            itemView.context.getString(
                R.string.track_subtitle_format,
                track.artistName,
                track.trackTime,
            )
    }
}
