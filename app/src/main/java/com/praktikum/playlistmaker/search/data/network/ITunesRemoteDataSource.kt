package com.praktikum.playlistmaker.search.data.network

import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.model.toTrack

class ITunesRemoteDataSource(
    private val apiService: ITunesApiService,
) : TrackRemoteDataSource {
    override suspend fun searchTracks(query: String): List<Track> {
        val response = apiService.search(term = query)
        return response.results.mapNotNull { it.toTrack() }
    }
}
