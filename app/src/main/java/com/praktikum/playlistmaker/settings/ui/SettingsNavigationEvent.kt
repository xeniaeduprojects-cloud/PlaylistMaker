package com.praktikum.playlistmaker.settings.ui

import androidx.annotation.StringRes

sealed class SettingsNavigationEvent {
    data class Share(
        @field:StringRes val messageResId: Int,
    ) : SettingsNavigationEvent()

    data class SendEmail(
        val email: String,
        @field:StringRes val subjectResId: Int,
        @field:StringRes val textResId: Int,
    ) : SettingsNavigationEvent()

    data class OpenUrl(
        val url: String,
    ) : SettingsNavigationEvent()

    data class ApplyTheme(
        val isDarkMode: Boolean,
    ) : SettingsNavigationEvent()
}
