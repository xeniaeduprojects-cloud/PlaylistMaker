package com.praktikum.playlistmaker.settings.data.repository

interface SettingsRepository {
    fun isDarkModeEnabled(): Boolean

    fun setDarkMode(enabled: Boolean)
}
