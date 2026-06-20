package com.praktikum.playlistmaker.settings.di

import android.content.Context
import android.content.SharedPreferences
import com.praktikum.playlistmaker.settings.data.repository.SettingsRepository
import com.praktikum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.praktikum.playlistmaker.settings.domain.GetDarkModeUseCase
import com.praktikum.playlistmaker.settings.domain.SetDarkModeUseCase
import com.praktikum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule =
    module {
        single<SharedPreferences> {
            get<Context>().getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
        }

        single<SettingsRepository> { SettingsRepositoryImpl(get()) }

        single { GetDarkModeUseCase(get()) }
        single { SetDarkModeUseCase(get()) }

        viewModelOf(::SettingsViewModel)
    }
