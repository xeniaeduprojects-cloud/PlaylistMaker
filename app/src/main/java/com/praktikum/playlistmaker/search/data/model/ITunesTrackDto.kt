package com.praktikum.playlistmaker.search.data.model

import com.google.gson.annotations.SerializedName

data class ITunesTrackDto(
    @SerializedName("trackId") val trackId: ULong?,
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("artistName") val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long?,
    @SerializedName("artworkUrl100") val artworkUrl100: String?,
)
