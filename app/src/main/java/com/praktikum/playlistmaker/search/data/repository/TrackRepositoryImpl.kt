package com.praktikum.playlistmaker.search.data.repository

import com.praktikum.playlistmaker.search.data.model.Result
import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.network.ITunesRemoteDataSource
import com.praktikum.playlistmaker.search.data.network.RetrofitProvider
import com.praktikum.playlistmaker.search.data.network.TrackRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TrackRepositoryImpl(
    private val remoteDataSource: TrackRemoteDataSource =
        ITunesRemoteDataSource(
            RetrofitProvider.iTunesApiService,
        ),
) : TrackRepository {
    override fun searchTracks(query: String): Flow<Result<List<Track>>> =
        flow {
            val result =
                safeRepositoryCall {
                    remoteDataSource.searchTracks(query)
                }
            emit(result)
        }.flowOn(Dispatchers.IO)
}
