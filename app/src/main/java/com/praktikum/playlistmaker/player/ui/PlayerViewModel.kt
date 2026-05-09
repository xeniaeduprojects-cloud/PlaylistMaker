package com.praktikum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.praktikum.playlistmaker.search.data.model.Track

class PlayerViewModel(
    track: Track,
) : ViewModel() {
    private val _uiState =
        MutableLiveData<PlayerUiState>(
            PlayerUiState.Content(
                trackName = track.trackName,
                artistName = track.artistName,
                duration = track.trackTime,
                album = track.collectionName,
                year = track.releaseDate,
                genre = track.primaryGenreName,
                country = track.country,
                artworkUrl = track.artworkUrl512,
            ),
        )
    val uiState: LiveData<PlayerUiState> = _uiState
}
