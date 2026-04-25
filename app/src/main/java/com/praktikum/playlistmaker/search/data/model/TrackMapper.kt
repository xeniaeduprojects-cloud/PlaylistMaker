package com.praktikum.playlistmaker.search.data.model

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

fun ITunesTrackDto.toTrack(): Track? {
    val requiredFields = listOf(trackId, trackName, artistName, artworkUrl100)
    if (requiredFields.any { it == null }) {
        Log.w(
            "TrackMapper",
            "Track has missing required fields: " +
                "trackId=$trackId, trackName=$trackName, " +
                "artistName=$artistName, artworkUrl100=$artworkUrl100",
        )
        return null
    }

    val trackTimeFormatted =
        trackTimeMillis?.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
        } ?: "00:00"

    return Track(
        trackId = trackId!!,
        trackName = trackName!!,
        artistName = artistName!!,
        trackTime = trackTimeFormatted,
        artworkUrl100 = artworkUrl100!!,
    )
}
