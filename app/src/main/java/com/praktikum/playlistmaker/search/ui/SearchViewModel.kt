package com.praktikum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praktikum.playlistmaker.search.data.repository.FakeTrackRepository
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val trackRepository = FakeTrackRepository()

    private val _uiState = MutableLiveData(SearchUiState())
    val uiState: LiveData<SearchUiState> = _uiState

    fun onSearchQueryChanged(query: String) {
        _uiState.value =
            _uiState.value?.copy(
                searchQuery = query,
                showClearButton = query.isNotEmpty(),
            )
        searchTracks(query)
    }

    fun onClearButtonClicked() {
        _uiState.value =
            _uiState.value?.copy(
                searchQuery = "",
                tracks = emptyList(),
                showClearButton = false,
            )
    }

    fun restoreSearchQuery(query: String) {
        _uiState.value =
            _uiState.value?.copy(
                searchQuery = query,
                showClearButton = query.isNotEmpty(),
            )
        if (query.isNotEmpty()) {
            searchTracks(query)
        }
    }

    private fun searchTracks(query: String) {
        viewModelScope.launch {
            trackRepository.searchTracks(query).collect { tracks ->
                _uiState.value = _uiState.value?.copy(tracks = tracks)
            }
        }
    }
}
