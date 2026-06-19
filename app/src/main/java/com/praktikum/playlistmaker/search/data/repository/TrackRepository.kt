package com.praktikum.playlistmaker.search.data.repository

import com.praktikum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
}
