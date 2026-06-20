package com.praktikum.playlistmaker.settings.domain

import com.praktikum.playlistmaker.settings.data.repository.SettingsRepository

class SetDarkModeUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(enabled: Boolean) {
        settingsRepository.setDarkMode(enabled)
    }
}
