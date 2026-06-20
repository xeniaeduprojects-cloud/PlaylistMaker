package com.praktikum.playlistmaker.settings.ui

import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.settings.data.repository.SettingsRepository
import com.praktikum.playlistmaker.settings.di.settingsModule
import com.praktikum.playlistmaker.settings.domain.GetDarkModeUseCase
import com.praktikum.playlistmaker.settings.domain.SetDarkModeUseCase
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
    private val fakeSettingsRepository = FakeSettingsRepository()

    private val testModule by lazy {
        module {
            single<SettingsRepository> { fakeSettingsRepository }
            single { GetDarkModeUseCase(get()) }
            single { SetDarkModeUseCase(get()) }
            viewModel { SettingsViewModel(get(), get()) }
        }
    }

    @Before
    fun setUp() {
        unloadKoinModules(settingsModule)
        loadKoinModules(testModule)
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
        unloadKoinModules(testModule)
        loadKoinModules(settingsModule)
    }

    @Test
    fun dark_theme_switch_is_checked_when_dark_mode_enabled() {
        fakeSettingsRepository.setDarkMode(true)

        launchSettingsActivity()

        onView(withId(R.id.switch_dark_theme)).check(matches(isChecked()))
    }

    @Test
    fun dark_theme_switch_is_unchecked_when_dark_mode_disabled() {
        fakeSettingsRepository.setDarkMode(false)

        launchSettingsActivity()

        onView(withId(R.id.switch_dark_theme)).check(matches(isNotChecked()))
    }

    @Test
    fun toggling_dark_theme_switch_saves_setting_to_repository() {
        fakeSettingsRepository.setDarkMode(false)

        launchSettingsActivity()

        onView(withId(R.id.switch_dark_theme)).perform(click())

        assert(fakeSettingsRepository.isDarkModeEnabled())
    }

    @Test
    fun toggling_dark_theme_switch_off_saves_setting_to_repository() {
        fakeSettingsRepository.setDarkMode(true)

        launchSettingsActivity()

        onView(withId(R.id.switch_dark_theme)).perform(click())

        assert(!fakeSettingsRepository.isDarkModeEnabled())
    }

    @Test
    fun share_button_launches_share_intent() {
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(
            Instrumentation.ActivityResult(0, null),
        )

        launchSettingsActivity()

        onView(withId(R.id.tv_share)).perform(click())

        intended(
            allOf(
                hasAction(Intent.ACTION_CHOOSER),
                hasExtra(
                    Intent.EXTRA_INTENT,
                    allOf(
                        hasAction(Intent.ACTION_SEND),
                        hasType("text/plain"),
                    ),
                ),
            ),
        )
    }

    @Test
    fun support_button_launches_email_intent_with_correct_email() {
        intending(hasAction(Intent.ACTION_SENDTO)).respondWith(
            Instrumentation.ActivityResult(0, null),
        )

        launchSettingsActivity()

        onView(withId(R.id.tv_support)).perform(click())

        intended(
            allOf(
                hasAction(Intent.ACTION_SENDTO),
                hasExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(SettingsViewModel.SUPPORT_EMAIL),
                ),
            ),
        )
    }

    @Test
    fun user_agreement_button_launches_browser_intent_with_correct_url() {
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(0, null),
        )

        launchSettingsActivity()

        onView(withId(R.id.tv_user_agreement)).perform(click())

        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(SettingsViewModel.USER_AGREEMENT_URL),
            ),
        )
    }

    @Test
    fun dark_theme_setting_persists_across_activity_recreation() {
        fakeSettingsRepository.setDarkMode(false)

        val scenario = ActivityScenario.launch(SettingsActivity::class.java)

        onView(withId(R.id.switch_dark_theme)).check(matches(isNotChecked()))
        onView(withId(R.id.switch_dark_theme)).perform(click())
        onView(withId(R.id.switch_dark_theme)).check(matches(isChecked()))

        scenario.recreate()

        onView(withId(R.id.switch_dark_theme)).check(matches(isChecked()))
    }

    @Test
    fun multiple_dark_theme_toggles_update_repository_correctly() {
        fakeSettingsRepository.setDarkMode(false)

        launchSettingsActivity()

        onView(withId(R.id.switch_dark_theme)).perform(click())
        assert(fakeSettingsRepository.isDarkModeEnabled())

        onView(withId(R.id.switch_dark_theme)).perform(click())
        assert(!fakeSettingsRepository.isDarkModeEnabled())

        onView(withId(R.id.switch_dark_theme)).perform(click())
        assert(fakeSettingsRepository.isDarkModeEnabled())
    }

    private fun launchSettingsActivity() {
        ActivityScenario.launch(SettingsActivity::class.java)
    }

    private class FakeSettingsRepository : SettingsRepository {
        private var darkModeEnabled = false

        override fun isDarkModeEnabled(): Boolean = darkModeEnabled

        override fun setDarkMode(enabled: Boolean) {
            darkModeEnabled = enabled
        }
    }
}
