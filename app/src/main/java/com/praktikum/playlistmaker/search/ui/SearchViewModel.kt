package com.praktikum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktikum.playlistmaker.search.data.model.Result
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import com.praktikum.playlistmaker.search.data.repository.TrackRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackRepository: TrackRepository = TrackRepositoryImpl(),
) : ViewModel() {
    private val _uiState = MutableLiveData(SearchUiState())
    val uiState: LiveData<SearchUiState> = _uiState

    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 300L
    }

    fun onSearchQueryRequested(query: String) {
        _uiState.value =
            _uiState.value?.copy(
                searchQuery = query,
                showClearButton = query.isNotEmpty(),
            )
        searchDebounced(query)
    }

    fun onClearButtonClicked() {
        searchJob?.cancel()
        _uiState.value =
            _uiState.value?.copy(
                searchQuery = "",
                tracks = emptyList(),
                showClearButton = false,
                showNoConnection = false,
                showNoResults = false,
            )
    }

    fun restoreSearchQuery(query: String) {
        _uiState.value =
            _uiState.value?.copy(
                searchQuery = query,
                showClearButton = query.isNotEmpty(),
            )
        if (query.isNotEmpty()) {
            searchDebounced(query)
        }
    }

    private fun searchDebounced(query: String) {
        searchJob?.cancel()
        if (query.isEmpty()) {
            _uiState.value =
                _uiState.value?.copy(
                    tracks = emptyList(),
                    showNoConnection = false,
                    showNoResults = false,
                )
            return
        }
        searchJob =
            viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY_MS)
                searchTracks(query)
            }
    }

    private suspend fun searchTracks(query: String) {
        _uiState.value =
            _uiState.value?.copy(
                showNoConnection = false,
                showNoResults = false,
            )

        trackRepository.searchTracks(query).collect { result ->
            when (result) {
                is Result.Success -> {
                    _uiState.value =
                        _uiState.value?.copy(
                            tracks = result.data,
                            showNoConnection = false,
                            showNoResults = result.data.isEmpty(),
                        )
                }
                is Result.Error -> {
                    _uiState.value =
                        _uiState.value?.copy(
                            tracks = emptyList(),
                            showNoConnection = true,
                            showNoResults = false,
                        )
                }
            }
        }
    }
}
