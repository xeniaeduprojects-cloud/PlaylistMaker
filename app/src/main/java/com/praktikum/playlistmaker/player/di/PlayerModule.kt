package com.praktikum.playlistmaker.player.di

import com.praktikum.playlistmaker.player.ui.PlayerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val playerModule =
    module {
        viewModelOf(::PlayerViewModel)
    }
