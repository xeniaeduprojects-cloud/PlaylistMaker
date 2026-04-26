package com.praktikum.playlistmaker.settings.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.settings.data.repository.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    @Before
    fun setUp() {
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = SettingsViewModel(fakeSettingsRepository)
    }

    @Test
    fun `initial state loads dark mode setting from repository`() {
        fakeSettingsRepository.setDarkMode(true)
        val viewModel = SettingsViewModel(fakeSettingsRepository)

        val state = viewModel.uiState.value

        assertNotNull(state)
        assertEquals(true, state?.isDarkModeEnabled)
    }

    @Test
    fun `initial state is false when dark mode is disabled`() {
        fakeSettingsRepository.setDarkMode(false)
        val viewModel = SettingsViewModel(fakeSettingsRepository)

        val state = viewModel.uiState.value

        assertNotNull(state)
        assertEquals(false, state?.isDarkModeEnabled)
    }

    @Test
    fun `onDarkModeToggled updates repository and ui state`() {
        viewModel.onDarkModeToggled(true)

        assertEquals(true, fakeSettingsRepository.isDarkModeEnabled())
        assertEquals(true, viewModel.uiState.value?.isDarkModeEnabled)
    }

    @Test
    fun `onDarkModeToggled to false updates repository and ui state`() {
        fakeSettingsRepository.setDarkMode(true)
        viewModel = SettingsViewModel(fakeSettingsRepository)

        viewModel.onDarkModeToggled(false)

        assertEquals(false, fakeSettingsRepository.isDarkModeEnabled())
        assertEquals(false, viewModel.uiState.value?.isDarkModeEnabled)
    }

    @Test
    fun `onShareClicked emits share navigation event with correct resource id`() {
        viewModel.onShareClicked()

        val event = viewModel.navigationEvent.value

        assertNotNull(event)
        assert(event is SettingsNavigationEvent.Share)
        assertEquals(R.string.share_message, (event as SettingsNavigationEvent.Share).messageResId)
    }

    @Test
    fun `onSupportClicked emits send email navigation event with correct data`() {
        viewModel.onSupportClicked()

        val event = viewModel.navigationEvent.value

        assertNotNull(event)
        assert(event is SettingsNavigationEvent.SendEmail)
        val emailEvent = event as SettingsNavigationEvent.SendEmail
        assertEquals(SettingsViewModel.SUPPORT_EMAIL, emailEvent.email)
        assertEquals(R.string.support_email_subject, emailEvent.subjectResId)
        assertEquals(R.string.support_email_text, emailEvent.textResId)
    }

    @Test
    fun `onUserAgreementClicked emits open url navigation event with correct url`() {
        viewModel.onUserAgreementClicked()

        val event = viewModel.navigationEvent.value

        assertNotNull(event)
        assert(event is SettingsNavigationEvent.OpenUrl)
        assertEquals(SettingsViewModel.USER_AGREEMENT_URL, (event as SettingsNavigationEvent.OpenUrl).url)
    }

    @Test
    fun `onNavigationEventHandled clears navigation event`() {
        viewModel.onShareClicked()
        assertNotNull(viewModel.navigationEvent.value)

        viewModel.onNavigationEventHandled()

        assertNull(viewModel.navigationEvent.value)
    }

    @Test
    fun `multiple navigation events can be triggered sequentially`() {
        viewModel.onShareClicked()
        assert(viewModel.navigationEvent.value is SettingsNavigationEvent.Share)

        viewModel.onNavigationEventHandled()
        assertNull(viewModel.navigationEvent.value)

        viewModel.onSupportClicked()
        assert(viewModel.navigationEvent.value is SettingsNavigationEvent.SendEmail)

        viewModel.onNavigationEventHandled()
        assertNull(viewModel.navigationEvent.value)

        viewModel.onUserAgreementClicked()
        assert(viewModel.navigationEvent.value is SettingsNavigationEvent.OpenUrl)
    }

    @Test
    fun `dark mode toggle multiple times updates state correctly`() {
        viewModel.onDarkModeToggled(true)
        assertEquals(true, viewModel.uiState.value?.isDarkModeEnabled)

        viewModel.onDarkModeToggled(false)
        assertEquals(false, viewModel.uiState.value?.isDarkModeEnabled)

        viewModel.onDarkModeToggled(true)
        assertEquals(true, viewModel.uiState.value?.isDarkModeEnabled)
    }

    @Test
    fun `support email constant has correct value`() {
        assertEquals("support@example.com", SettingsViewModel.SUPPORT_EMAIL)
    }

    @Test
    fun `user agreement url constant has correct value`() {
        assertEquals("https://yandex.ru/legal/practicum_offer/ru/", SettingsViewModel.USER_AGREEMENT_URL)
    }

    private class FakeSettingsRepository : SettingsRepository {
        private var darkModeEnabled = false

        override fun isDarkModeEnabled(): Boolean = darkModeEnabled

        override fun setDarkMode(enabled: Boolean) {
            darkModeEnabled = enabled
        }
    }
}
