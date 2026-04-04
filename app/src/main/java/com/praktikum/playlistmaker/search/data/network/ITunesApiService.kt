package com.praktikum.playlistmaker.search.data.network

import com.praktikum.playlistmaker.search.data.model.ITunesSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
    ): ITunesSearchResponse
}
