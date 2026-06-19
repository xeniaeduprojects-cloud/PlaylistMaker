package com.praktikum.playlistmaker.player.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktikum.playlistmaker.player.domain.GetCurrentPositionUseCase
import com.praktikum.playlistmaker.player.domain.PauseTrackUseCase
import com.praktikum.playlistmaker.player.domain.PlayTrackUseCase
import com.praktikum.playlistmaker.player.domain.PreparePlayerUseCase
import com.praktikum.playlistmaker.player.domain.ReleasePlayerUseCase
import com.praktikum.playlistmaker.player.domain.SeekToPositionUseCase
import com.praktikum.playlistmaker.search.data.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import com.praktikum.playlistmaker.player.data.media.PlayerState as MediaPlayerState

@Suppress("LongParameterList")
class PlayerViewModel(
    track: Track,
    private val preparePlayerUseCase: PreparePlayerUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    private val pauseTrackUseCase: PauseTrackUseCase,
    private val seekToPositionUseCase: SeekToPositionUseCase,
    private val getCurrentPositionUseCase: GetCurrentPositionUseCase,
    private val releasePlayerUseCase: ReleasePlayerUseCase,
    private val positionFlowProvider: () -> Flow<Int> = {
        flow {
            while (true) {
                emit((getCurrentPositionUseCase() / MILLIS_IN_SECOND).toInt())
                delay(POSITION_UPDATE_DELAY_MS)
            }
        }
    },
) : ViewModel() {
    companion object {
        private const val TAG = "PlayerViewModel"
        private const val MILLIS_IN_SECOND = 1000L
        private const val POSITION_UPDATE_DELAY_MS = 300L
    }

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

    private val _playbackState = MutableLiveData<PlaybackState>(PlaybackState.Idle)
    val playbackState: LiveData<PlaybackState> = _playbackState

    private var positionUpdateJob: Job? = null

    fun prepare(url: String) {
        Log.d(TAG, "prepare: $url")
        preparePlayerUseCase(url) { playerState ->
            Log.d(TAG, "Player state callback: $playerState")
            _playbackState.value =
                when (playerState) {
                    MediaPlayerState.IDLE -> PlaybackState.Idle
                    MediaPlayerState.BUFFERING -> PlaybackState.Buffering
                    MediaPlayerState.READY -> PlaybackState.Paused
                    MediaPlayerState.PLAYING -> {
                        startPositionUpdates()
                        PlaybackState.Playing
                    }
                    MediaPlayerState.PAUSED -> {
                        stopPositionUpdates()
                        PlaybackState.Paused
                    }
                    MediaPlayerState.ENDED -> {
                        stopPositionUpdates()
                        _uiState.value =
                            PlayerUiState.Paused(
                                content = trackContent,
                                currentPositionSeconds = 0,
                            )
                        PlaybackState.Paused
                    }
                    MediaPlayerState.ERROR -> {
                        stopPositionUpdates()
                        PlaybackState.Error
                    }
                }
        }
    }

    fun onPlayPauseClick() {
        val currentState = _uiState.value ?: return
        Log.d(TAG, "onPlayPauseClick: currentState=$currentState")
        when (currentState) {
            is PlayerUiState.Paused -> {
                Log.d(TAG, "Starting playback")
                if (currentState.currentPositionSeconds == 0) {
                    seekToPositionUseCase(0)
                }
                playTrackUseCase()
                startPositionUpdates()
                _uiState.value =
                    PlayerUiState.Playing(
                        content = trackContent,
                        currentPositionSeconds = currentState.currentPositionSeconds,
                    )
            }
            is PlayerUiState.Playing -> pausePlayer()
        }
    }

    fun pausePlayer() {
        val currentState = _uiState.value ?: return
        if (currentState is PlayerUiState.Playing) {
            Log.d(TAG, "Pausing playback")
            pauseTrackUseCase()
            stopPositionUpdates()
            _uiState.value =
                PlayerUiState.Paused(
                    content = trackContent,
                    currentPositionSeconds = currentState.currentPositionSeconds,
                )
        }
    }

    private fun startPositionUpdates() {
        Log.d(TAG, "startPositionUpdates")
        stopPositionUpdates()
        positionUpdateJob =
            viewModelScope.launch {
                positionFlowProvider().collect { positionSeconds ->
                    val currentState = _uiState.value
                    if (currentState is PlayerUiState.Playing) {
                        _uiState.value = currentState.copy(currentPositionSeconds = positionSeconds)
                    }
                }
            }
    }

    private fun stopPositionUpdates() {
        Log.d(TAG, "stopPositionUpdates")
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPositionUpdates()
        releasePlayerUseCase()
    }
}
