package com.praktikum.playlistmaker.search.ui

import com.praktikum.playlistmaker.search.data.model.Result
import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeTrackRepositoryAndroidTest : TrackRepository {
    private val responses = mutableMapOf<String, Result<List<Track>>>()
    val searchCallCount = mutableMapOf<String, Int>()

    fun setResponse(
        query: String,
        result: Result<List<Track>>,
    ) {
        responses[query] = result
    }

    fun resetCallCount() {
        searchCallCount.clear()
    }

    override fun searchTracks(query: String): Flow<Result<List<Track>>> {
        searchCallCount[query] = (searchCallCount[query] ?: 0) + 1
        return flowOf(responses[query] ?: Result.Success(emptyList()))
    }
}
