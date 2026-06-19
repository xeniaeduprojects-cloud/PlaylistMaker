package com.praktikum.playlistmaker.search.data.model

import android.util.Log
import com.praktikum.playlistmaker.search.domain.model.Track
import com.praktikum.playlistmaker.util.formatMillisToMinutesSeconds

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
            formatMillisToMinutesSeconds(it)
        } ?: "00:00"

    @Suppress("MagicNumber")
    val year = releaseDate?.substring(0, 4)

    return Track(
        trackId = trackId!!,
        trackName = trackName!!,
        artistName = artistName!!,
        trackTime = trackTimeFormatted,
        artworkUrl100 = artworkUrl100!!,
        collectionName = collectionName,
        releaseDate = year,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl,
    )
}
