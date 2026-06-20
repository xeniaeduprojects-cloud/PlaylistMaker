package com.praktikum.playlistmaker.settings.domain

import com.praktikum.playlistmaker.settings.data.repository.SettingsRepository

class GetDarkModeUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Boolean = settingsRepository.isDarkModeEnabled()
}
