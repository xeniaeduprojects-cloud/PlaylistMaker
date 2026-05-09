package com.praktikum.playlistmaker.player.ui

sealed class PlayerUiState {
    data class Content(
        val trackName: String,
        val artistName: String,
        val duration: String,
        val album: String?,
        val year: String?,
        val genre: String?,
        val country: String?,
        val artworkUrl: String,
    ) : PlayerUiState()
}
