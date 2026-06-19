package com.praktikum.playlistmaker.search.data.network

import com.praktikum.playlistmaker.search.domain.model.Track

interface TrackRemoteDataSource {
    suspend fun searchTracks(query: String): List<Track>
}
