package com.praktikum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.settings.data.repository.SettingsRepository

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<SettingsUiState>()
    val uiState: LiveData<SettingsUiState> = _uiState

    private val _navigationEvent = MutableLiveData<SettingsNavigationEvent?>()
    val navigationEvent: LiveData<SettingsNavigationEvent?> = _navigationEvent

    init {
        loadSettings()
    }

    companion object {
        const val SUPPORT_EMAIL = "support@example.com"
        const val USER_AGREEMENT_URL = "https://yandex.ru/legal/practicum_offer/ru/"
    }

    private fun loadSettings() {
        val isDarkMode = settingsRepository.isDarkModeEnabled()
        _uiState.value = SettingsUiState(isDarkModeEnabled = isDarkMode)
    }

    fun onDarkModeToggled(enabled: Boolean) {
        settingsRepository.setDarkMode(enabled)
        _uiState.value = SettingsUiState(isDarkModeEnabled = enabled)
        _navigationEvent.value = SettingsNavigationEvent.ApplyTheme(enabled)
    }

    fun onShareClicked() {
        _navigationEvent.value = SettingsNavigationEvent.Share(R.string.share_message)
    }

    fun onSupportClicked() {
        _navigationEvent.value =
            SettingsNavigationEvent.SendEmail(
                SUPPORT_EMAIL,
                R.string.support_email_subject,
                R.string.support_email_text,
            )
    }

    fun onUserAgreementClicked() {
        _navigationEvent.value = SettingsNavigationEvent.OpenUrl(USER_AGREEMENT_URL)
    }

    fun onNavigationEventHandled() {
        _navigationEvent.value = null
    }
}
