package com.praktikum.playlistmaker.search.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.praktikum.playlistmaker.search.data.model.Result
import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import com.praktikum.playlistmaker.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val fakeTrackRepository = FakeTrackRepository()
    private val viewModel = SearchViewModel(fakeTrackRepository)

    @Test
    fun `initial state is idle`() {
        val state = viewModel.uiState.value

        assertTrue(state is SearchUiState.Idle)
    }

    @Test
    fun `search success emits loading then content`() {
        val query = "metallica"
        val expectedTracks = listOf(track("One"))
        fakeTrackRepository.setResponse(query, Result.Success(expectedTracks))

        viewModel.onSearchQueryRequested(query)

        assertTrue(viewModel.uiState.value is SearchUiState.Loading)

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertTrue(finalState is SearchUiState.Content)
        assertEquals(expectedTracks, (finalState as SearchUiState.Content).tracks)
    }

    @Test
    fun `search empty result emits loading then empty`() {
        val query = "unknown"
        fakeTrackRepository.setResponse(query, Result.Success(emptyList()))

        viewModel.onSearchQueryRequested(query)

        assertTrue(viewModel.uiState.value is SearchUiState.Loading)

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Empty)
    }

    @Test
    fun `search failure emits loading then error`() {
        val query = "nirvana"
        fakeTrackRepository.setResponse(query, Result.Error("network error"))

        viewModel.onSearchQueryRequested(query)

        assertTrue(viewModel.uiState.value is SearchUiState.Loading)

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Error)
    }

    @Test
    fun `clear action resets state and cancels pending search`() {
        val query = "queen"
        fakeTrackRepository.setResponse(query, Result.Success(listOf(track("Bohemian Rhapsody"))))

        viewModel.onSearchQueryRequested(query)
        viewModel.onClearButtonClicked()

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Idle)
        assertTrue(fakeTrackRepository.requestedQueries.isEmpty())
    }

    @Test
    fun `debounce processes only latest query`() {
        fakeTrackRepository.setResponse("first", Result.Success(listOf(track("First Track"))))
        fakeTrackRepository.setResponse("second", Result.Success(listOf(track("Second Track"))))

        viewModel.onSearchQueryRequested("first")
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(150)
        viewModel.onSearchQueryRequested("second")

        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf("second"), fakeTrackRepository.requestedQueries)

        val finalState = viewModel.uiState.value
        assertTrue(finalState is SearchUiState.Content)
        assertEquals("second", finalState?.searchQuery)
    }

    @Test
    fun `empty query resets to idle and does not call repository`() {
        viewModel.onSearchQueryRequested("")

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Idle)
        assertTrue(fakeTrackRepository.requestedQueries.isEmpty())
    }

    @Test
    fun `restore empty query sets idle and does not call repository`() {
        viewModel.restoreSearchQuery("")

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is SearchUiState.Idle)
        assertTrue(fakeTrackRepository.requestedQueries.isEmpty())
    }

    @Test
    fun `restore non-empty query debounces then executes search`() {
        val query = "abc"
        val expectedTracks = listOf(track("Recovered Track"))
        fakeTrackRepository.setResponse(query, Result.Success(expectedTracks))

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
        assertTrue(finalState is SearchUiState.Content)
        assertEquals(expectedTracks, (finalState as SearchUiState.Content).tracks)
    }

    private fun track(name: String): Track =
        Track(
            trackName = name,
            artistName = "Artist",
            trackTime = "3:00",
            artworkUrl100 = "https://example.com/artwork.jpg",
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
            return responses[query] ?: flowOf(Result.Success(emptyList()))
        }
    }
}
