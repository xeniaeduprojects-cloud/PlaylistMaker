package com.praktikum.playlistmaker.player.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.praktikum.playlistmaker.player.data.media.AudioPlayerManager
import com.praktikum.playlistmaker.player.data.media.PlayerState
import com.praktikum.playlistmaker.search.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeAudioPlayerManager: FakeAudioPlayerManager
    private lateinit var viewModel: PlayerViewModel

    private val testTrack =
        Track(
            trackId = 1L,
            trackName = "Test Track",
            artistName = "Test Artist",
            trackTime = "3:30",
            artworkUrl100 = "http://test.com/art.jpg",
            collectionName = "Test Album",
            releaseDate = "2023",
            primaryGenreName = "Rock",
            country = "USA",
            previewUrl = "http://test.com/preview.mp3",
        )

    private val testTrackNoUrl = testTrack.copy(previewUrl = null)

    @Before
    fun setup() {
        fakeAudioPlayerManager = FakeAudioPlayerManager()
    }

    private fun buildViewModel(
        track: Track = testTrack,
        positionFlowProvider: () -> Flow<Int> = { emptyFlow() },
    ) = PlayerViewModel(track, fakeAudioPlayerManager, positionFlowProvider)

    @Test
    fun when_track_has_no_url_ui_shows_error_state() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel = buildViewModel(track = testTrackNoUrl)
            advanceUntilIdle()

            var uiState: PlayerUiState? = null
            viewModel.uiState.observeForever { uiState = it }

            assertTrue(uiState is PlayerUiState.Paused)
            assertFalse(fakeAudioPlayerManager.prepareCalled)
        }

    @Test
    fun pressing_play_when_ready_calls_play_on_manager() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel = buildViewModel()
            viewModel.uiState.observeForever { }

            viewModel.prepare("http://test.com/preview.mp3")
            advanceUntilIdle()

            fakeAudioPlayerManager.emitState(PlayerState.READY)
            advanceUntilIdle()

            viewModel.onPlayPauseClick()
            advanceUntilIdle()

            assertTrue(fakeAudioPlayerManager.playCalled)
        }

    @Test
    fun pressing_pause_when_playing_calls_pause_on_manager() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel = buildViewModel()
            viewModel.uiState.observeForever { }

            viewModel.prepare("http://test.com/preview.mp3")
            advanceUntilIdle()

            fakeAudioPlayerManager.emitState(PlayerState.READY)
            advanceUntilIdle()

            viewModel.onPlayPauseClick()
            advanceUntilIdle()

            viewModel.onPlayPauseClick()
            advanceUntilIdle()

            assertTrue(fakeAudioPlayerManager.pauseCalled)
        }

    @Test
    fun when_player_ends_ui_state_resets_to_ready() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel = buildViewModel()
            viewModel.prepare("http://test.com/preview.mp3")
            advanceUntilIdle()

            var uiState: PlayerUiState? = null
            viewModel.uiState.observeForever { uiState = it }

            fakeAudioPlayerManager.emitState(PlayerState.PLAYING)
            advanceUntilIdle()

            fakeAudioPlayerManager.emitState(PlayerState.ENDED)
            advanceUntilIdle()

            assertTrue(uiState is PlayerUiState.Paused)
            assertEquals(0, (uiState as PlayerUiState.Paused).currentPositionSeconds)
        }

    @Test
    fun when_player_errors_ui_shows_error_state() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel = buildViewModel()
            viewModel.prepare("http://test.com/preview.mp3")
            advanceUntilIdle()

            var playbackState: PlaybackState? = null
            viewModel.playbackState.observeForever { playbackState = it }

            fakeAudioPlayerManager.emitState(PlayerState.ERROR)
            advanceUntilIdle()

            assertTrue(playbackState is PlaybackState.Error)
        }

    @Test
    fun position_updates_periodically_while_playing() =
        runTest(mainDispatcherRule.testDispatcher) {
            val positionChannel = Channel<Int>(Channel.UNLIMITED)
            viewModel = buildViewModel(positionFlowProvider = { positionChannel.receiveAsFlow() })
            viewModel.prepare("http://test.com/preview.mp3")
            advanceUntilIdle()

            var uiState: PlayerUiState? = null
            viewModel.uiState.observeForever { uiState = it }

            fakeAudioPlayerManager.emitState(PlayerState.READY)
            advanceUntilIdle()

            viewModel.onPlayPauseClick()
            advanceUntilIdle()

            positionChannel.send(1)
            advanceUntilIdle()

            assertTrue(uiState is PlayerUiState.Playing)
            assertEquals(1, (uiState as PlayerUiState.Playing).currentPositionSeconds)

            positionChannel.send(3)
            advanceUntilIdle()

            val uiState2 = uiState
            assertTrue(uiState2 is PlayerUiState.Playing)
            assertEquals(3, (uiState2 as PlayerUiState.Playing).currentPositionSeconds)
        }

    @Test
    fun position_stops_updating_when_paused() =
        runTest(mainDispatcherRule.testDispatcher) {
            val positionChannel = Channel<Int>(Channel.UNLIMITED)
            viewModel = buildViewModel(positionFlowProvider = { positionChannel.receiveAsFlow() })
            viewModel.prepare("http://test.com/preview.mp3")
            advanceUntilIdle()

            var uiState: PlayerUiState? = null
            viewModel.uiState.observeForever { uiState = it }

            fakeAudioPlayerManager.emitState(PlayerState.READY)
            advanceUntilIdle()

            viewModel.onPlayPauseClick()
            advanceUntilIdle()

            positionChannel.send(2)
            advanceUntilIdle()

            assertTrue(uiState is PlayerUiState.Playing)
            assertEquals(2, (uiState as PlayerUiState.Playing).currentPositionSeconds)

            viewModel.onPlayPauseClick()
            advanceUntilIdle()

            positionChannel.send(5)
            advanceUntilIdle()

            val uiStatePaused = uiState
            assertTrue(uiStatePaused is PlayerUiState.Paused)
            assertEquals(2, (uiStatePaused as PlayerUiState.Paused).currentPositionSeconds)
        }

    @Test
    fun on_cleared_calls_release_on_manager() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel = buildViewModel()
            viewModel.prepare("http://test.com/preview.mp3")
            advanceUntilIdle()

            val onClearedMethod = PlayerViewModel::class.java.getDeclaredMethod("onCleared")
            onClearedMethod.isAccessible = true
            onClearedMethod.invoke(viewModel)
            advanceUntilIdle()

            assertTrue(fakeAudioPlayerManager.releaseCalled)
        }

    private class FakeAudioPlayerManager : AudioPlayerManager {
        var prepareCalled = false
        var playCalled = false
        var pauseCalled = false
        var releaseCalled = false
        var positionMs = 0L
        private var stateCallback: ((PlayerState) -> Unit)? = null

        override fun prepare(
            url: String,
            onStateChanged: (PlayerState) -> Unit,
        ) {
            prepareCalled = true
            stateCallback = onStateChanged
        }

        override fun play() {
            playCalled = true
        }

        override fun pause() {
            pauseCalled = true
        }

        override fun release() {
            releaseCalled = true
        }

        override fun isPlaying(): Boolean = false

        override fun getCurrentPosition(): Long = positionMs

        @Suppress("EmptyFunctionBlock")
        override fun seekTo(positionMs: Long) {}

        fun emitState(state: PlayerState) {
            stateCallback?.invoke(state)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
