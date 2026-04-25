package com.praktikum.playlistmaker.search.data.model

data class Track(
    val trackId: ULong,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
)
