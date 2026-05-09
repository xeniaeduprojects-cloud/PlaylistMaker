package com.praktikum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepository
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackRepository: TrackRepository,
    private val trackHistoryRepository: TrackHistoryRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<SearchUiState>(SearchUiState.HistoryContent(emptyList()))
    val uiState: LiveData<SearchUiState> = _uiState

    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 300L
    }

    fun onSearchQueryRequested(query: String) {
        if (query.isEmpty()) {
            searchJob?.cancel()
            val trackHistory = trackHistoryRepository.getHistory().toList()
            _uiState.value = SearchUiState.HistoryContent(trackHistory)
            return
        }

        _uiState.value =
            SearchUiState.Loading(
                searchQuery = query,
                tracks = currentTracks(),
            )
        searchDebounced(query)
    }

    fun onClearButtonClicked() {
        searchJob?.cancel()
        val trackHistory = trackHistoryRepository.getHistory().toList()
        _uiState.value = SearchUiState.HistoryContent(trackHistory)
    }

    fun onSearchTextEditInFocus() {
        val trackHistory = trackHistoryRepository.getHistory().toList()
        _uiState.value = SearchUiState.HistoryContent(trackHistory)
    }

    fun restoreSearchQuery(query: String) {
        if (query.isEmpty()) {
            val trackHistory = trackHistoryRepository.getHistory().toList()
            _uiState.value = SearchUiState.HistoryContent(trackHistory)
            return
        }

        _uiState.value =
            SearchUiState.Loading(
                searchQuery = query,
                tracks = currentTracks(),
            )
        searchDebounced(query)
    }

    private fun searchDebounced(query: String) {
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY_MS)
                searchTracks(query)
            }
    }

    private suspend fun searchTracks(query: String) {
        trackRepository.searchTracks(query).collect { result ->
            result
                .onSuccess { tracks ->
                    _uiState.value =
                        if (tracks.isEmpty()) {
                            SearchUiState.SearchEmpty(
                                searchQuery = query,
                            )
                        } else {
                            SearchUiState.SearchContent(
                                searchQuery = query,
                                tracks = tracks,
                            )
                        }
                }.onFailure {
                    _uiState.value =
                        SearchUiState.Error(
                            searchQuery = query,
                        )
                }
        }
    }

    private fun currentTracks(): List<Track> =
        when (val state = _uiState.value) {
            is SearchUiState.SearchContent -> state.tracks
            is SearchUiState.Loading -> state.tracks
            else -> emptyList()
        }

    fun onTrackClick(track: Track) {
        trackHistoryRepository.addTrack(track)
    }

    fun onClearHistoryClicked() {
        trackHistoryRepository.clearHistory()
        _uiState.value = SearchUiState.HistoryContent(emptyList())
    }
}
