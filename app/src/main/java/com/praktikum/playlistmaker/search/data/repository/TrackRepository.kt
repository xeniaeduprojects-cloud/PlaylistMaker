package com.praktikum.playlistmaker.search.data.repository

import com.praktikum.playlistmaker.search.data.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(query: String): Flow<List<Track>>
}
