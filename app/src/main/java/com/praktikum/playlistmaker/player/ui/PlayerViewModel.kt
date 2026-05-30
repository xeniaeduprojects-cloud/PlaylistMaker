package com.praktikum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.praktikum.playlistmaker.search.data.model.Track

class PlayerViewModel(
    track: Track,
) : ViewModel() {
    private val trackContent =
        TrackContent(
            trackName = track.trackName,
            artistName = track.artistName,
            duration = track.trackTime,
            album = track.collectionName,
            year = track.releaseDate,
            genre = track.primaryGenreName,
            country = track.country,
            artworkUrl = track.artworkUrl512,
        )

    private val _uiState =
        MutableLiveData<PlayerUiState>(
            PlayerUiState.Paused(
                content = trackContent,
                currentPositionSeconds = 0,
            ),
        )
    val uiState: LiveData<PlayerUiState> = _uiState

    fun onPlayPauseClick() {
        val currentState = _uiState.value ?: return
        _uiState.value =
            when (currentState) {
                is PlayerUiState.Paused ->
                    PlayerUiState.Playing(
                        content = trackContent,
                        currentPositionSeconds = currentState.currentPositionSeconds,
                    )
                is PlayerUiState.Playing ->
                    PlayerUiState.Paused(
                        content = trackContent,
                        currentPositionSeconds = currentState.currentPositionSeconds,
                    )
            }
    }
}
