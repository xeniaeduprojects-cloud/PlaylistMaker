package com.praktikum.playlistmaker.search.data.model

import com.google.gson.annotations.SerializedName

data class ITunesSearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<ITunesTrackDto>,
)
