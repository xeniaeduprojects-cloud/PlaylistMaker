package com.praktikum.playlistmaker.search.di

import com.praktikum.playlistmaker.search.data.network.ITunesApiService
import com.praktikum.playlistmaker.search.data.network.ITunesRemoteDataSource
import com.praktikum.playlistmaker.search.data.network.RetrofitProvider
import com.praktikum.playlistmaker.search.data.network.TrackRemoteDataSource
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import com.praktikum.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.praktikum.playlistmaker.search.ui.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchUiModule =
    module {
        single<ITunesApiService> { RetrofitProvider.iTunesApiService }
        single<TrackRemoteDataSource> { ITunesRemoteDataSource(get()) }
        single<TrackRepository> { TrackRepositoryImpl(get()) }

        viewModelOf(::SearchViewModel)
    }
