package com.praktikum.playlistmaker.player.di

import com.praktikum.playlistmaker.player.data.media.AudioPlayerManager
import com.praktikum.playlistmaker.player.data.media.AudioPlayerManagerImpl
import com.praktikum.playlistmaker.player.domain.GetCurrentPositionUseCase
import com.praktikum.playlistmaker.player.domain.PauseTrackUseCase
import com.praktikum.playlistmaker.player.domain.PlayTrackUseCase
import com.praktikum.playlistmaker.player.domain.PreparePlayerUseCase
import com.praktikum.playlistmaker.player.domain.ReleasePlayerUseCase
import com.praktikum.playlistmaker.player.domain.SeekToPositionUseCase
import com.praktikum.playlistmaker.player.ui.PlayerViewModel
import com.praktikum.playlistmaker.search.data.model.Track
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerModule =
    module {
        // The AudioPlayerManager (and its single ExoPlayer) must be shared by every
        // use case of one player screen, otherwise prepare() and play() would target
        // different ExoPlayer instances and no audio would play. We build it once per
        // PlayerViewModel here so it lives and dies with the ViewModel (released via
        // ReleasePlayerUseCase in onCleared).
        viewModel { params ->
            val audioPlayerManager: AudioPlayerManager = AudioPlayerManagerImpl(get())
            PlayerViewModel(
                track = params.get<Track>(),
                preparePlayerUseCase = PreparePlayerUseCase(audioPlayerManager),
                playTrackUseCase = PlayTrackUseCase(audioPlayerManager),
                pauseTrackUseCase = PauseTrackUseCase(audioPlayerManager),
                seekToPositionUseCase = SeekToPositionUseCase(audioPlayerManager),
                getCurrentPositionUseCase = GetCurrentPositionUseCase(audioPlayerManager),
                releasePlayerUseCase = ReleasePlayerUseCase(audioPlayerManager),
            )
        }
    }
