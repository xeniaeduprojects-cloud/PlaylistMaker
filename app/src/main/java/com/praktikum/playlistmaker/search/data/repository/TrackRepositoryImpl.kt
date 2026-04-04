package com.praktikum.playlistmaker.search.data.repository

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
    override fun searchTracks(query: String): Flow<List<Track>> =
        flow {
            val tracks = remoteDataSource.searchTracks(query)
            emit(tracks)
        }.flowOn(Dispatchers.IO)
}
