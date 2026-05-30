package com.praktikum.playlistmaker.player.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepository
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import com.praktikum.playlistmaker.search.di.searchModule
import com.praktikum.playlistmaker.search.ui.SearchActivity
import com.praktikum.playlistmaker.search.ui.SearchViewModel
import com.praktikum.playlistmaker.search.ui.TrackRepositoryAndroidTest
import com.praktikum.playlistmaker.util.RecentSet
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class PlayerNavigationTest {
    private val fakeTrackRepository = TrackRepositoryAndroidTest()
    private val fakeTrackHistoryRepository = FakeTrackHistoryRepository()

    private val testModule by lazy {
        module {
            single<TrackRepository> { fakeTrackRepository }
            single<TrackHistoryRepository> { fakeTrackHistoryRepository }
            viewModel { SearchViewModel(get(), get(), searchDebounceDelayMs = 0L) }
        }
    }

    @Before
    fun setup() {
        unloadKoinModules(searchModule)
        loadKoinModules(testModule)
    }

    @After
    fun tearDown() {
        unloadKoinModules(testModule)
        loadKoinModules(searchModule)
    }

    @Test
    fun clicking_track_in_search_results_opens_player() {
        val query = "test"
        val track =
            Track(
                trackId = 1L,
                trackName = "Test Track",
                artistName = "Test Artist",
                trackTime = "03:45",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = "Test Album",
                releaseDate = "2020",
                primaryGenreName = "Rock",
                country = "USA",
            )

        fakeTrackRepository.setResponse(query, Result.success(listOf(track)))

        ActivityScenario.launch(SearchActivity::class.java)

        onView(withId(R.id.et_search)).perform(typeText(query), closeSoftKeyboard())

        onView(withId(R.id.rv_tracks)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()),
        )

        onView(withId(R.id.img_album_art)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_track_title)).check(matches(isDisplayed()))
    }

    @Test
    fun clicking_track_in_history_opens_player() {
        val track =
            Track(
                trackId = 1L,
                trackName = "History Track",
                artistName = "History Artist",
                trackTime = "04:20",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = "History Album",
                releaseDate = "2019",
                primaryGenreName = "Pop",
                country = "UK",
            )

        fakeTrackHistoryRepository.addTrack(track)

        ActivityScenario.launch(SearchActivity::class.java)

        onView(withId(R.id.et_search)).perform(click())

        onView(withId(R.id.rv_tracks)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()),
        )

        onView(withId(R.id.img_album_art)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_track_title)).check(matches(isDisplayed()))
    }

    @Test
    fun player_survives_configuration_change() {
        val track =
            Track(
                trackId = 1L,
                trackName = "Config Track",
                artistName = "Config Artist",
                trackTime = "05:00",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = "Config Album",
                releaseDate = "2021",
                primaryGenreName = "Jazz",
                country = "France",
            )

        val scenario =
            ActivityScenario.launch<PlayerActivity>(
                PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
            )

        onView(withId(R.id.img_album_art)).check(matches(isDisplayed()))

        scenario.recreate()

        onView(withId(R.id.img_album_art)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_track_title)).check(matches(isDisplayed()))
    }

    @Test
    fun clicking_back_button_returns_to_search() {
        val query = "test"
        val track =
            Track(
                trackId = 1L,
                trackName = "Back Test Track",
                artistName = "Back Test Artist",
                trackTime = "03:00",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = "Back Test Album",
                releaseDate = "2022",
                primaryGenreName = "Electronic",
                country = "Germany",
            )

        fakeTrackRepository.setResponse(query, Result.success(listOf(track)))

        ActivityScenario.launch(SearchActivity::class.java)

        onView(withId(R.id.et_search)).perform(typeText(query), closeSoftKeyboard())

        onView(withId(R.id.rv_tracks)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()),
        )

        onView(withId(R.id.img_album_art)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_back)).perform(click())

        onView(withId(R.id.et_search)).check(matches(isDisplayed()))
    }

    @Test
    fun pressing_system_back_returns_to_search() {
        val query = "back"
        val track =
            Track(
                trackId = 1L,
                trackName = "System Back Track",
                artistName = "System Back Artist",
                trackTime = "04:15",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = "System Back Album",
                releaseDate = "2023",
                primaryGenreName = "Classical",
                country = "Austria",
            )

        fakeTrackRepository.setResponse(query, Result.success(listOf(track)))

        ActivityScenario.launch(SearchActivity::class.java)

        onView(withId(R.id.et_search)).perform(typeText(query), closeSoftKeyboard())

        onView(withId(R.id.rv_tracks)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()),
        )

        onView(withId(R.id.img_album_art)).check(matches(isDisplayed()))

        androidx.test.espresso.Espresso
            .pressBackUnconditionally()

        onView(withId(R.id.et_search)).check(matches(isDisplayed()))
    }

    private class FakeTrackHistoryRepository : TrackHistoryRepository {
        private val history = RecentSet<Long, Track>(10) { it.trackId }

        override fun getHistory(): RecentSet<Long, Track> = history

        override fun addTrack(track: Track) {
            history.put(track)
        }

        override fun clearHistory() {
        }
    }
}
