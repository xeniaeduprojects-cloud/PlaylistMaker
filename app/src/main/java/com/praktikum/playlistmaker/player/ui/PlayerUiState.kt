package com.praktikum.playlistmaker.player.ui

data class TrackContent(
    val trackName: String,
    val artistName: String,
    val duration: String,
    val album: String?,
    val year: String?,
    val genre: String?,
    val country: String?,
    val artworkUrl: String,
)

sealed class PlayerUiState {
    data class Playing(
        val content: TrackContent,
        val currentPositionSeconds: Int,
    ) : PlayerUiState()

    data class Paused(
        val content: TrackContent,
        val currentPositionSeconds: Int,
    ) : PlayerUiState()
}
