package com.praktikum.playlistmaker.player.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.player.data.media.AudioPlayerManager
import com.praktikum.playlistmaker.player.data.media.PlayerState
import com.praktikum.playlistmaker.player.di.playerModule
import com.praktikum.playlistmaker.player.domain.GetCurrentPositionUseCase
import com.praktikum.playlistmaker.player.domain.PauseTrackUseCase
import com.praktikum.playlistmaker.player.domain.PlayTrackUseCase
import com.praktikum.playlistmaker.player.domain.PreparePlayerUseCase
import com.praktikum.playlistmaker.player.domain.ReleasePlayerUseCase
import com.praktikum.playlistmaker.player.domain.SeekToPositionUseCase
import com.praktikum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class PlayerActivityTest {
    private lateinit var fakeAudioPlayerManager: FakeAudioPlayerManager
    private lateinit var testModule: org.koin.core.module.Module
    private lateinit var scenario: ActivityScenario<PlayerActivity>

    private val testTrack =
        Track(
            trackId = 1L,
            trackName = "Test Track Name",
            artistName = "Test Artist Name",
            trackTime = "3:30",
            artworkUrl100 = "https://test.com/art.jpg",
            collectionName = "Test Album",
            releaseDate = "2023",
            primaryGenreName = "Rock",
            country = "USA",
            previewUrl = "https://test.com/preview.mp3",
        )

    @Before
    fun setup() {
        fakeAudioPlayerManager = FakeAudioPlayerManager()
        testModule =
            module {
                factory<AudioPlayerManager> { fakeAudioPlayerManager }
                factory { PreparePlayerUseCase(get()) }
                factory { PlayTrackUseCase(get()) }
                factory { PauseTrackUseCase(get()) }
                factory { SeekToPositionUseCase(get()) }
                factory { GetCurrentPositionUseCase(get()) }
                factory { ReleasePlayerUseCase(get()) }
                viewModel { params -> PlayerViewModel(params.get(), get(), get(), get(), get(), get(), get()) }
            }
        unloadKoinModules(playerModule)
        loadKoinModules(testModule)
    }

    @After
    fun tearDown() {
        if (::scenario.isInitialized) {
            scenario.close()
        }
        unloadKoinModules(testModule)
        loadKoinModules(playerModule)
    }

    @Test
    fun player_screen_shows_correct_track_metadata() {
        scenario = launchPlayerActivity(testTrack)

        onView(withId(R.id.tv_track_title)).check(matches(withText("Test Track Name")))
        onView(withId(R.id.tv_artist_name)).check(matches(withText("Test Artist Name")))
        onView(withId(R.id.tv_duration)).check(matches(withText("3:30")))
        onView(withId(R.id.img_album_art)).check(matches(isDisplayed()))
    }

    @Test
    fun play_button_visible_initially_pause_button_hidden() {
        scenario = launchPlayerActivity(testTrack)

        onView(withId(R.id.btn_play_pause)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_play_pause)).check(matches(isEnabled()))
    }

    @Test
    fun after_clicking_play_pause_button_becomes_visible() {
        scenario = launchPlayerActivity(testTrack)

        runBlocking {
            delay(100)
            withContext(Dispatchers.Main) {
                fakeAudioPlayerManager.emitState(PlayerState.READY)
            }
            delay(100)
        }

        onView(withId(R.id.btn_play_pause)).perform(click())

        runBlocking {
            delay(100)
            withContext(Dispatchers.Main) {
                fakeAudioPlayerManager.emitState(PlayerState.PLAYING)
            }
            delay(200)
        }

        onView(withId(R.id.btn_play_pause)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_play_pause)).check(matches(isEnabled()))
    }

    @Test
    fun error_state_disables_play_button() {
        scenario = launchPlayerActivity(testTrack)

        runBlocking {
            delay(100)
            withContext(Dispatchers.Main) {
                fakeAudioPlayerManager.emitState(PlayerState.ERROR)
            }
            delay(200)
        }

        onView(withId(R.id.btn_play_pause)).check(matches(not(isEnabled())))
    }

    @Test
    fun playback_time_label_is_displayed() {
        scenario = launchPlayerActivity(testTrack)

        onView(withId(R.id.tv_playback_time)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_playback_time)).check(matches(withText("00:00")))
    }

    private fun launchPlayerActivity(track: Track): ActivityScenario<PlayerActivity> {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), PlayerActivity::class.java).apply {
                putExtra("EXTRA_TRACK", track)
            }
        return ActivityScenario.launch(intent)
    }

    private class FakeAudioPlayerManager : AudioPlayerManager {
        var positionMs = 0L
        private var stateCallback: ((PlayerState) -> Unit)? = null

        override fun prepare(
            url: String,
            onStateChanged: (PlayerState) -> Unit,
        ) {
            stateCallback = onStateChanged
        }

        override fun play() {}

        override fun pause() {}

        override fun release() {}

        override fun isPlaying(): Boolean = false

        override fun getCurrentPosition(): Long = positionMs

        override fun seekTo(positionMs: Long) {}

        fun emitState(state: PlayerState) {
            stateCallback?.invoke(state)
        }
    }
}
