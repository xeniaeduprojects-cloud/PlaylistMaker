package com.praktikum.playlistmaker.search.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.praktikum.playlistmaker.search.data.network.ITunesApiService
import com.praktikum.playlistmaker.search.data.network.ITunesRemoteDataSource
import com.praktikum.playlistmaker.search.data.network.TrackRemoteDataSource
import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepository
import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepositoryImpl
import com.praktikum.playlistmaker.search.data.repository.TrackRepository
import com.praktikum.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.praktikum.playlistmaker.search.ui.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule =
    module {
        single<Gson> { Gson() }

        single<SharedPreferences> {
            get<Context>().getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
        }

        single<Retrofit> {
            Retrofit
                .Builder()
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(GsonConverterFactory.create(get()))
                .build()
        }

        single<ITunesApiService> {
            get<Retrofit>().create(ITunesApiService::class.java)
        }

        single<TrackRemoteDataSource> { ITunesRemoteDataSource(get()) }
        single<TrackRepository> { TrackRepositoryImpl(get()) }
        single<TrackHistoryRepository> { TrackHistoryRepositoryImpl(get(), get()) }

        viewModelOf(::SearchViewModel)
    }
