package com.praktikum.playlistmaker.search.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepository
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import com.praktikum.playlistmaker.search.domain.AddTrackToHistoryUseCase
import com.praktikum.playlistmaker.search.domain.ClearSearchHistoryUseCase
import com.praktikum.playlistmaker.search.domain.GetSearchHistoryUseCase
import com.praktikum.playlistmaker.search.domain.SearchTracksUseCase
import com.praktikum.playlistmaker.search.domain.model.Track
import com.praktikum.playlistmaker.util.RecentSet
import com.praktikum.playlistmaker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val fakeTrackRepository = FakeTrackRepository()
    private val fakeTrackHistoryRepository = FakeTrackHistoryRepository()
    private val viewModel =
        SearchViewModel(
            searchTracksUseCase = SearchTracksUseCase(fakeTrackRepository),
            getSearchHistoryUseCase = GetSearchHistoryUseCase(fakeTrackHistoryRepository),
            addTrackToHistoryUseCase = AddTrackToHistoryUseCase(fakeTrackHistoryRepository),
            clearSearchHistoryUseCase = ClearSearchHistoryUseCase(fakeTrackHistoryRepository),
        )

    @Test
    fun `initial state is history content`() {
        val state = viewModel.uiState.value

        assertTrue(state is SearchUiState.HistoryContent)
    }

    @Test
    fun `search success emits loading then content`() {
        val query = "metallica"
        val expectedTracks = listOf(track("One"))
        fakeTrackRepository.setResponse(query, Result.success(expectedTracks))

        viewModel.onSearchQueryRequested(query)

        assertTrue(viewModel.uiState.value is SearchUiState.Loading)

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertTrue(finalState is SearchUiState.SearchContent)
        assertEquals(expectedTracks, (finalState as SearchUiState.SearchContent).tracks)
    }

    @Test
    fun `search empty result emits loading then empty`() {
        val query = "unknown"
        fakeTrackRepository.setResponse(query, Result.success(emptyList()))

        viewModel.onSearchQueryRequested(query)

        assertTrue(viewModel.uiState.value is SearchUiState.Loading)

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.SearchEmpty)
    }

    @Test
    fun `search failure emits loading then error`() {
        val query = "nirvana"
        val httpException = HttpException(Response.error<Any>(500, "".toResponseBody()))
        fakeTrackRepository.setResponse(query, Result.failure(httpException))

        viewModel.onSearchQueryRequested(query)

        assertTrue(viewModel.uiState.value is SearchUiState.Loading)

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Error)
    }

    @Test
    fun `clear action resets state and cancels pending search`() {
        val query = "queen"
        fakeTrackRepository.setResponse(query, Result.success(listOf(track("Bohemian Rhapsody"))))

        viewModel.onSearchQueryRequested(query)
        viewModel.onClearButtonClicked()

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.HistoryContent)
        assertTrue(fakeTrackRepository.requestedQueries.isEmpty())
    }

    @Test
    fun `debounce processes only latest query`() {
        fakeTrackRepository.setResponse("first", Result.success(listOf(track("First Track"))))
        fakeTrackRepository.setResponse("second", Result.success(listOf(track("Second Track"))))

        viewModel.onSearchQueryRequested("first")
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(150)
        viewModel.onSearchQueryRequested("second")

        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf("second"), fakeTrackRepository.requestedQueries)

        val finalState = viewModel.uiState.value
        assertTrue(finalState is SearchUiState.SearchContent)
        assertEquals("second", finalState?.searchQuery)
    }

    @Test
    fun `empty query resets to history content and does not call repository`() {
        viewModel.onSearchQueryRequested("")

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.HistoryContent)
        assertTrue(fakeTrackRepository.requestedQueries.isEmpty())
    }

    @Test
    fun `restore empty query sets history content and does not call repository`() {
        viewModel.restoreSearchQuery("")

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.HistoryContent)
        assertTrue(fakeTrackRepository.requestedQueries.isEmpty())
    }

    @Test
    fun `restore non-empty query debounces then executes search`() {
        val query = "abc"
        val expectedTracks = listOf(track("Recovered Track"))
        fakeTrackRepository.setResponse(query, Result.success(expectedTracks))

        viewModel.restoreSearchQuery(query)

        val loadingState = viewModel.uiState.value
        assertTrue(loadingState is SearchUiState.Loading)
        assertEquals(query, loadingState?.searchQuery)
        assertTrue(fakeTrackRepository.requestedQueries.isEmpty())

        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(299)
        assertTrue(fakeTrackRepository.requestedQueries.isEmpty())

        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(1)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(query), fakeTrackRepository.requestedQueries)
        val finalState = viewModel.uiState.value
        assertTrue(finalState is SearchUiState.SearchContent)
        assertEquals(expectedTracks, (finalState as SearchUiState.SearchContent).tracks)
    }

    @Test
    fun `onSearchTextEditInFocus sets history content state`() {
        val track1 = track("Track 1")
        val track2 = track("Track 2")
        fakeTrackHistoryRepository.addTrack(track1)
        fakeTrackHistoryRepository.addTrack(track2)

        viewModel.onSearchTextEditInFocus()

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.HistoryContent)

        val historyContent = state as SearchUiState.HistoryContent
        assertEquals(2, historyContent.tracks.size)
        assertEquals(track2, historyContent.tracks[0])
        assertEquals(track1, historyContent.tracks[1])
    }

    @Test
    fun `onSearchTextEditInFocus with empty history sets empty history content`() {
        viewModel.onSearchTextEditInFocus()

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.HistoryContent)
        assertTrue((state as SearchUiState.HistoryContent).tracks.isEmpty())
    }

    @Test
    fun `onTrackClick adds track to history`() {
        val track1 = track("Track 1")

        viewModel.onTrackClick(track1)

        val history = fakeTrackHistoryRepository.getHistory().toList()
        assertEquals(1, history.size)
        assertEquals(track1, history[0])
    }

    @Test
    fun `onClearHistoryClicked clears history and sets empty history content`() {
        val track1 = track("Track 1")
        fakeTrackHistoryRepository.addTrack(track1)

        viewModel.onClearHistoryClicked()

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.HistoryContent)
        assertTrue((state as SearchUiState.HistoryContent).tracks.isEmpty())
    }

    private fun track(name: String): Track =
        Track(
            trackId = name.hashCode().toLong(),
            trackName = name,
            artistName = "Artist",
            trackTime = "3:00",
            artworkUrl100 = "https://example.com/artwork.jpg",
            collectionName = null,
            releaseDate = null,
            primaryGenreName = null,
            country = null,
            previewUrl = null,
        )

    private class FakeTrackRepository : TrackRepository {
        private val responses = mutableMapOf<String, Flow<Result<List<Track>>>>()
        val requestedQueries = mutableListOf<String>()

        fun setResponse(
            query: String,
            result: Result<List<Track>>,
        ) {
            responses[query] = flowOf(result)
        }

        override fun searchTracks(query: String): Flow<Result<List<Track>>> {
            requestedQueries += query
            return responses[query] ?: flowOf(Result.success(emptyList()))
        }
    }

    private class FakeTrackHistoryRepository : TrackHistoryRepository {
        private var history = RecentSet<Long, Track>(10) { it.trackId }

        override fun getHistory(): RecentSet<Long, Track> = history

        override fun addTrack(track: Track) {
            history.put(track)
        }

        override fun clearHistory() {
            history = RecentSet(10) { it.trackId }
        }
    }
}
