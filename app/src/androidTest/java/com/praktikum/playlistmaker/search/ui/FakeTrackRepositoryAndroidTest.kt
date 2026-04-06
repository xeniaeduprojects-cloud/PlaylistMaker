package com.praktikum.playlistmaker.search.ui

import com.praktikum.playlistmaker.search.data.model.Result
import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeTrackRepositoryAndroidTest : TrackRepository {
    private val responses = mutableMapOf<String, Result<List<Track>>>()

    fun setResponse(
        query: String,
        result: Result<List<Track>>,
    ) {
        responses[query] = result
    }

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flowOf(responses[query] ?: Result.Success(emptyList()))
}
