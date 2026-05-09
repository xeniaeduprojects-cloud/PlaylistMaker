package com.praktikum.playlistmaker.search.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
) : Parcelable {
    val artworkUrl512: String
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
