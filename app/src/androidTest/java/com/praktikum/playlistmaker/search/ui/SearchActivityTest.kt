package com.praktikum.playlistmaker.search.ui

import android.text.Layout
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.search.data.model.Result
import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import com.praktikum.playlistmaker.search.di.searchUiModule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class SearchActivityTest {
    private val fakeTrackRepository = FakeTrackRepositoryAndroidTest()

    private val testModule by lazy {
        module {
            single<TrackRepository> { fakeTrackRepository }
            viewModel { SearchViewModel(get()) }
        }
    }

    @Before
    fun setUp() {
        unloadKoinModules(searchUiModule)
        loadKoinModules(testModule)
    }

    @After
    fun tearDown() {
        unloadKoinModules(testModule)
        loadKoinModules(searchUiModule)
    }

    @Test
    fun typing_query_shows_clear_button() {
        launchSearchActivity()

        onView(withId(R.id.searchEditText)).perform(typeText("metallica"), closeSoftKeyboard())

        onView(withId(R.id.searchClearButton)).check(matches(isDisplayed()))
    }

    @Test
    fun search_empty_result_shows_nothing_found_state() {
        val query = "unknown"
        fakeTrackRepository.setResponse(query, Result.Success(emptyList()))

        launchSearchActivity()

        onView(withId(R.id.searchEditText)).perform(typeText(query), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(350))

        onView(withId(R.id.nothingFoundText)).check(matches(isDisplayed()))
        onView(withId(R.id.noConnectionLayout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun search_error_shows_no_connection_state() {
        val query = "offline"
        fakeTrackRepository.setResponse(query, Result.Error("network error"))

        launchSearchActivity()

        onView(withId(R.id.searchEditText)).perform(typeText(query), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(350))

        onView(withId(R.id.noConnectionLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.nothingFoundText)).check(matches(not(isDisplayed())))
    }

    @Test
    fun long_track_name_is_truncated_with_ellipsis() {
        val query = "long"
        val longTrackTitle =
            "This Is A Very Very Very Long Track Name That Must Be Truncated In Single Line Mode"
        fakeTrackRepository.setResponse(
            query,
            Result.Success(
                listOf(
                    track(
                        name = longTrackTitle,
                    ),
                ),
            ),
        )

        launchSearchActivity()

        onView(withId(R.id.searchEditText)).perform(typeText(query), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(350))

        onView(withId(R.id.trackTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.trackTitle)).check(matches(isEllipsized()))
    }

    @Test
    fun long_artist_name_is_truncated_with_ellipsis() {
        val query = "artist"
        val longArtistName =
            "Very Long Artist Name That Should Not Fit Into One Line In The Subtitle TextView"
        fakeTrackRepository.setResponse(
            query,
            Result.Success(
                listOf(
                    track(
                        name = "Song",
                        artist = longArtistName,
                    ),
                ),
            ),
        )

        launchSearchActivity()

        onView(withId(R.id.searchEditText)).perform(typeText(query), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(350))

        onView(withId(R.id.trackSubtitle)).check(matches(isDisplayed()))
        onView(withId(R.id.trackSubtitle)).check(matches(isEllipsized()))
    }

    @Test
    fun clear_button_clears_query_and_hides_itself() {
        launchSearchActivity()

        onView(withId(R.id.searchEditText)).perform(typeText("abba"), closeSoftKeyboard())
        onView(withId(R.id.searchClearButton)).perform(click())

        onView(withId(R.id.searchEditText)).check(matches(withText("")))
        onView(withId(R.id.searchClearButton)).check(matches(not(isDisplayed())))
    }

    @Test
    fun refresh_button_retries_last_failed_search() {
        val query = "offline"
        fakeTrackRepository.setResponse(query, Result.Error("network error"))

        launchSearchActivity()

        onView(withId(R.id.searchEditText)).perform(typeText(query), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(350))

        onView(withId(R.id.noConnectionLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed()))

        fakeTrackRepository.setResponse(query, Result.Success(listOf(track("Recovered Song"))))
        fakeTrackRepository.resetCallCount()

        onView(withId(R.id.refreshButton)).perform(click())
        onView(isRoot()).perform(waitFor(350))

        onView(withId(R.id.noConnectionLayout)).check(matches(not(isDisplayed())))
        onView(withId(R.id.trackTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.trackTitle)).check(matches(withText("Recovered Song")))
    }

    @Test
    fun track_time_displays_in_mm_ss_format() {
        val query = "format"
        fakeTrackRepository.setResponse(
            query,
            Result.Success(
                listOf(
                    Track(
                        trackName = "Test Song",
                        artistName = "Test Artist",
                        trackTime = "03:45",
                        artworkUrl100 = "https://example.com/art.jpg",
                    ),
                ),
            ),
        )

        launchSearchActivity()

        onView(withId(R.id.searchEditText)).perform(typeText(query), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(350))

        onView(withId(R.id.trackSubtitle)).check(matches(isDisplayed()))
        onView(withId(R.id.trackSubtitle)).check(matches(withText("Test Artist • 03:45")))
    }

    private fun launchSearchActivity() {
        ActivityScenario.launch(SearchActivity::class.java)
    }

    private fun waitFor(delayMs: Long): ViewAction =
        object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()

            override fun getDescription(): String = "wait for $delayMs milliseconds"

            override fun perform(
                uiController: UiController,
                view: View,
            ) {
                uiController.loopMainThreadForAtLeast(delayMs)
            }
        }

    private fun isEllipsized(): Matcher<View> =
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("TextView text should be ellipsized")
            }

            override fun matchesSafely(view: View): Boolean {
                if (view !is TextView) return false
                val layout: Layout = view.layout ?: return false
                if (layout.lineCount == 0) return false

                return (0 until layout.lineCount).any { line ->
                    layout.getEllipsisCount(line) > 0
                }
            }
        }

    private fun track(
        name: String,
        artist: String = "Artist",
    ): Track =
        Track(
            trackName = name,
            artistName = artist,
            trackTime = "03:30",
            artworkUrl100 = "https://example.com/art.jpg",
        )
}
