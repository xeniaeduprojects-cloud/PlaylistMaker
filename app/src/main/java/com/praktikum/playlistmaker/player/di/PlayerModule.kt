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
import com.praktikum.playlistmaker.search.domain.model.Track
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerModule =
    module {
        factory<AudioPlayerManager> { AudioPlayerManagerImpl(get()) }

        factory { PreparePlayerUseCase(get()) }
        factory { PlayTrackUseCase(get()) }
        factory { PauseTrackUseCase(get()) }
        factory { SeekToPositionUseCase(get()) }
        factory { GetCurrentPositionUseCase(get()) }
        factory { ReleasePlayerUseCase(get()) }

        viewModel { params ->
            PlayerViewModel(params.get<Track>(), get(), get(), get(), get(), get(), get())
        }
    }
