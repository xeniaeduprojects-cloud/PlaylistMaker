package com.praktikum.playlistmaker.settings.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : SettingsRepository {
    override fun isDarkModeEnabled(): Boolean = sharedPreferences.getBoolean(KEY_DARK_MODE, false)

    override fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_DARK_MODE, enabled)
        }
    }

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
    }
}
