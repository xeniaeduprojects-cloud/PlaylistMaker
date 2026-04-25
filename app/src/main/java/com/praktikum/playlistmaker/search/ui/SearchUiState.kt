package com.praktikum.playlistmaker.search.ui

import com.praktikum.playlistmaker.search.data.model.Track

sealed class SearchUiState {
    abstract val searchQuery: String

    data class Idle(
        override val searchQuery: String = "",
    ) : SearchUiState()

    data class Loading(
        override val searchQuery: String,
        val tracks: List<Track> = emptyList(),
    ) : SearchUiState()

    data class SearchContent(
        override val searchQuery: String,
        val tracks: List<Track>,
    ) : SearchUiState()

    data class SearchEmpty(
        override val searchQuery: String,
    ) : SearchUiState()

    data class HistoryContent(
        val tracks: List<Track>,
        override val searchQuery: String = "",
    ) : SearchUiState()

    data class Error(
        override val searchQuery: String,
    ) : SearchUiState()
}
