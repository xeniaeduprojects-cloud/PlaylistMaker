package com.praktikum.playlistmaker.search.data.repository

import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.network.TrackRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val remoteDataSource: TrackRemoteDataSource,
) : TrackRepository {
    override fun searchTracks(query: String): Flow<Result<List<Track>>> =
        flow {
            val result =
                safeRepositoryCall {
                    remoteDataSource.searchTracks(query)
                }
            emit(result)
        }
}
