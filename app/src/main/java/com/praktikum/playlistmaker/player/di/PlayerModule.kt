package com.praktikum.playlistmaker.player.di

import com.praktikum.playlistmaker.player.data.media.AudioPlayerManager
import com.praktikum.playlistmaker.player.data.media.AudioPlayerManagerImpl
import com.praktikum.playlistmaker.player.ui.PlayerViewModel
import com.praktikum.playlistmaker.search.data.model.Track
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerModule =
    module {
        factory<AudioPlayerManager> { AudioPlayerManagerImpl(get()) }
        viewModel { params -> PlayerViewModel(params.get<Track>(), get()) }
    }
