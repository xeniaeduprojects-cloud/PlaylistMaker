package com.praktikum.playlistmaker.search.ui

import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import com.praktikum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryAndroidTest : TrackRepository {
    private val responses = mutableMapOf<String, Result<List<Track>>>()
    val searchCallCount = mutableMapOf<String, Int>()
    private var delayMs: Long = 0

    fun setResponse(
        query: String,
        result: Result<List<Track>>,
    ) {
        responses[query] = result
    }

    fun resetCallCount() {
        searchCallCount.clear()
    }

    fun setDelay(delayMs: Long) {
        this.delayMs = delayMs
    }

    override fun searchTracks(query: String): Flow<Result<List<Track>>> {
        searchCallCount[query] = (searchCallCount[query] ?: 0) + 1
        return flow {
            if (delayMs > 0) {
                delay(delayMs)
            }
            emit(responses[query] ?: Result.success(emptyList()))
        }
    }
}
