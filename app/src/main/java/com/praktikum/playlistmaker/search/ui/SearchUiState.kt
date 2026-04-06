package com.praktikum.playlistmaker.search.ui

import com.praktikum.playlistmaker.search.data.model.Track

sealed interface SearchUiState {
    val searchQuery: String
    val tracks: List<Track>
    val showClearButton: Boolean
    val showNoConnection: Boolean
    val showNoResults: Boolean

    data class Idle(
        override val searchQuery: String = "",
        override val showClearButton: Boolean = false,
    ) : SearchUiState {
        override val tracks: List<Track> = emptyList()
        override val showNoConnection: Boolean = false
        override val showNoResults: Boolean = false
    }

    data class Loading(
        override val searchQuery: String,
        override val showClearButton: Boolean,
        override val tracks: List<Track> = emptyList(),
    ) : SearchUiState {
        override val showNoConnection: Boolean = false
        override val showNoResults: Boolean = false
    }

    data class Content(
        override val searchQuery: String,
        override val showClearButton: Boolean,
        override val tracks: List<Track>,
    ) : SearchUiState {
        override val showNoConnection: Boolean = false
        override val showNoResults: Boolean = false
    }

    data class Empty(
        override val searchQuery: String,
        override val showClearButton: Boolean,
    ) : SearchUiState {
        override val tracks: List<Track> = emptyList()
        override val showNoConnection: Boolean = false
        override val showNoResults: Boolean = true
    }

    data class Error(
        override val searchQuery: String,
        override val showClearButton: Boolean,
    ) : SearchUiState {
        override val tracks: List<Track> = emptyList()
        override val showNoConnection: Boolean = true
        override val showNoResults: Boolean = false
    }
}
