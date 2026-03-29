package com.praktikum.playlistmaker.search.ui

import com.praktikum.playlistmaker.search.data.model.Track

data class SearchUiState(
    val searchQuery: String = "",
    val tracks: List<Track> = emptyList(),
    val showClearButton: Boolean = false,
)
