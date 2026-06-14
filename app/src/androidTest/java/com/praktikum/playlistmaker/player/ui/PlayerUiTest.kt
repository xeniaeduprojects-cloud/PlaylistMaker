package com.praktikum.playlistmaker.player.ui

import android.text.Layout
import android.view.View
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.search.data.model.Track
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerUiTest {
    @Test
    fun all_ui_elements_displayed_with_full_track_data() {
        val track =
            Track(
                trackId = 1L,
                trackName = "Yesterday",
                artistName = "The Beatles",
                trackTime = "02:05",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = "Help!",
                releaseDate = "1965",
                primaryGenreName = "Rock",
                country = "UK",
                previewUrl = "https://example.com/preview.mp3",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.btn_back)).check(matches(isDisplayed()))

        onView(withId(R.id.img_album_art)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_track_title)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_track_title)).check(matches(withText("Yesterday")))

        onView(withId(R.id.tv_artist_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_artist_name)).check(matches(withText("The Beatles")))

        onView(withId(R.id.tv_duration)).perform(nestedScrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.tv_duration)).check(matches(withText("02:05")))

        onView(withId(R.id.tv_album)).perform(nestedScrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.tv_album)).check(matches(withText("Help!")))
        onView(withId(R.id.label_album)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_year)).perform(nestedScrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.tv_year)).check(matches(withText("1965")))
        onView(withId(R.id.label_year)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_genre)).perform(nestedScrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.tv_genre)).check(matches(withText("Rock")))
        onView(withId(R.id.label_genre)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_country)).perform(nestedScrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.tv_country)).check(matches(withText("UK")))
        onView(withId(R.id.label_country)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_add_to_playlist)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_favourite)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_play_pause)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_playback_time)).check(matches(isDisplayed()))
    }

    private fun nestedScrollTo(): ViewAction =
        object : ViewAction {
            override fun getConstraints(): Matcher<View> =
                ViewMatchers.isDescendantOfA(ViewMatchers.isAssignableFrom(NestedScrollView::class.java))

            override fun getDescription(): String = "scroll to view in NestedScrollView"

            override fun perform(
                uiController: UiController,
                view: View,
            ) {
                val nestedScrollView = findParentNestedScrollView(view)
                if (nestedScrollView != null) {
                    nestedScrollView.scrollTo(0, view.top)
                    uiController.loopMainThreadUntilIdle()
                }
            }

            private fun findParentNestedScrollView(view: View): NestedScrollView? {
                var parent = view.parent
                while (parent != null) {
                    if (parent is NestedScrollView) {
                        return parent
                    }
                    parent = parent.parent
                }
                return null
            }
        }

    @Test
    fun album_row_hidden_when_collection_name_is_null() {
        val track =
            Track(
                trackId = 1L,
                trackName = "Test Track",
                artistName = "Test Artist",
                trackTime = "03:00",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = null,
                releaseDate = "2020",
                primaryGenreName = "Pop",
                country = "USA",
                previewUrl = "https://example.com/preview.mp3",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.label_album)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_album)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_year)).perform(nestedScrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.label_year)).check(matches(isDisplayed()))
    }

    @Test
    fun year_row_hidden_when_release_date_is_null() {
        val track =
            Track(
                trackId = 1L,
                trackName = "Test Track",
                artistName = "Test Artist",
                trackTime = "03:00",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = "Test Album",
                releaseDate = null,
                primaryGenreName = "Pop",
                country = "USA",
                previewUrl = "https://example.com/preview.mp3",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.label_year)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_year)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_album)).perform(nestedScrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.label_album)).check(matches(isDisplayed()))
    }

    @Test
    fun genre_row_hidden_when_primary_genre_name_is_null() {
        val track =
            Track(
                trackId = 1L,
                trackName = "Test Track",
                artistName = "Test Artist",
                trackTime = "03:00",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = "Test Album",
                releaseDate = "2020",
                primaryGenreName = null,
                country = "USA",
                previewUrl = "https://example.com/preview.mp3",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.label_genre)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_genre)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_country)).perform(nestedScrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.label_country)).check(matches(isDisplayed()))
    }

    @Test
    fun country_row_hidden_when_country_is_null() {
        val track =
            Track(
                trackId = 1L,
                trackName = "Test Track",
                artistName = "Test Artist",
                trackTime = "03:00",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = "Test Album",
                releaseDate = "2020",
                primaryGenreName = "Pop",
                country = null,
                previewUrl = "https://example.com/preview.mp3",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.label_country)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_country)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_genre)).perform(nestedScrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.label_genre)).check(matches(isDisplayed()))
    }

    @Test
    fun long_track_title_is_truncated_with_ellipsis() {
        val track =
            track(
                name = "This Is An Extremely Long Track Name That Cannot Possibly Fit On Two Lines Of Text On A Normal Screen",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.tv_track_title)).check(matches(isEllipsized()))
    }

    @Test
    fun long_artist_name_is_truncated_with_ellipsis() {
        val track =
            track(
                artist = "Very Very Very Very Very Long Artist Name That Should Not Fit On A Single Line",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.tv_artist_name)).check(matches(isEllipsized()))
    }

    @Test
    fun long_album_name_is_truncated_with_ellipsis() {
        val track =
            track(
                collectionName = "An Exceptionally Long Album Title That Will Definitely Not Fit In The Available Space Next To The Label",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.tv_album)).perform(nestedScrollTo()).check(matches(isEllipsized()))
    }

    @Test
    fun long_genre_is_truncated_with_ellipsis() {
        val track =
            track(
                primaryGenreName = "Psychedelic Progressive Alternative Experimental Electronic Rock And Roll",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.tv_genre)).perform(nestedScrollTo()).check(matches(isEllipsized()))
    }

    @Test
    fun long_country_is_truncated_with_ellipsis() {
        val track =
            track(
                country = "The United Kingdom Of Great Britain And Northern Ireland And Some Other Territories",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.tv_country)).perform(nestedScrollTo()).check(matches(isEllipsized()))
    }

    @Test
    fun long_album_does_not_overlap_label() {
        val track =
            track(
                collectionName = "An Exceptionally Long Album Title That Will Definitely Not Fit In The Available Space Next To The Label",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.tv_album))
            .perform(nestedScrollTo())
            .check(doesNotOverlapWith(R.id.label_album))
    }

    @Test
    fun long_genre_does_not_overlap_label() {
        val track =
            track(
                primaryGenreName = "Psychedelic Progressive Alternative Experimental Electronic Rock And Roll",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.tv_genre))
            .perform(nestedScrollTo())
            .check(doesNotOverlapWith(R.id.label_genre))
    }

    @Test
    fun long_country_does_not_overlap_label() {
        val track =
            track(
                country = "The United Kingdom Of Great Britain And Northern Ireland And Some Other Territories",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.tv_country))
            .perform(nestedScrollTo())
            .check(doesNotOverlapWith(R.id.label_country))
    }

    @Test
    fun all_optional_rows_hidden_when_all_null() {
        val track =
            Track(
                trackId = 1L,
                trackName = "Minimal Track",
                artistName = "Minimal Artist",
                trackTime = "01:30",
                artworkUrl100 = "https://example.com/art.jpg",
                collectionName = null,
                releaseDate = null,
                primaryGenreName = null,
                country = null,
                previewUrl = "https://example.com/preview.mp3",
            )

        ActivityScenario.launch<PlayerActivity>(
            PlayerActivity.createIntent(InstrumentationRegistry.getInstrumentation().targetContext, track),
        )

        onView(withId(R.id.tv_track_title)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_artist_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_duration)).check(matches(isDisplayed()))

        onView(withId(R.id.label_album)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_album)).check(matches(not(isDisplayed())))
        onView(withId(R.id.label_year)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_year)).check(matches(not(isDisplayed())))
        onView(withId(R.id.label_genre)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_genre)).check(matches(not(isDisplayed())))
        onView(withId(R.id.label_country)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_country)).check(matches(not(isDisplayed())))
    }

    private fun track(
        name: String = "Track Name",
        artist: String = "Artist Name",
        collectionName: String? = "Album Name",
        releaseDate: String? = "2020",
        primaryGenreName: String? = "Pop",
        country: String? = "USA",
    ): Track =
        Track(
            trackId = 1L,
            trackName = name,
            artistName = artist,
            trackTime = "03:00",
            artworkUrl100 = "https://example.com/art.jpg",
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = "https://example.com/preview.mp3",
        )

    private fun doesNotOverlapWith(labelId: Int): ViewAssertion =
        ViewAssertion { valueView, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException

            val labelView = valueView.rootView.findViewById<View>(labelId)

            val labelLoc = IntArray(2)
            labelView.getLocationOnScreen(labelLoc)
            val labelRight = labelLoc[0] + labelView.width

            val valueLoc = IntArray(2)
            valueView.getLocationOnScreen(valueLoc)
            val valueLeft = valueLoc[0]

            assert(valueLeft >= labelRight) {
                "Value view left (${valueLeft}px) overlaps label right (${labelRight}px)"
            }
        }

    private fun isEllipsized(): TypeSafeMatcher<View> =
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
}
