package com.praktikum.playlistmaker.search.domain

import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import kotlinx.coroutines.flow.Flow

class SearchTracksUseCase(
    private val trackRepository: TrackRepository,
) {
    operator fun invoke(query: String): Flow<Result<List<Track>>> = trackRepository.searchTracks(query)
}
