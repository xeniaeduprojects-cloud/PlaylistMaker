package com.praktikum.playlistmaker.search.data.model

import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("ForbiddenComment")
fun ITunesTrackDto.toTrack(): Track? {
    if (trackId == null || trackName == null || artistName == null || artworkUrl100 == null) {
        // TODO: how to handle null values? should it be exception  here?
        return null
    }

    val trackTimeFormatted =
        trackTimeMillis?.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
        } ?: "00:00"

    return Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTimeFormatted,
        artworkUrl100 = artworkUrl100,
    )
}
