package com.praktikum.playlistmaker.search.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.util.RecentSet

class TrackHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : TrackHistoryRepository {
    private val gson = Gson()

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(TRACKS_HISTORY_KEY, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    override fun saveHistory(tracks: List<Track>) {
        val recentTracks = RecentSet.fromList(tracks, 10) { it.trackId }
        val json = gson.toJson(recentTracks.toList())
        sharedPreferences.edit { putString(TRACKS_HISTORY_KEY, json) }
    }

    companion object {
        private const val TRACKS_HISTORY_KEY = "tracks_history"
    }
}
