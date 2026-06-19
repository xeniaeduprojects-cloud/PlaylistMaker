package com.praktikum.playlistmaker.search.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.praktikum.playlistmaker.search.domain.model.Track
import com.praktikum.playlistmaker.util.RecentSet

class TrackHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val capacity: Int = HISTORY_CAPACITY,
) : TrackHistoryRepository {
    override fun getHistory(): RecentSet<Long, Track> {
        val json =
            sharedPreferences.getString(TRACKS_HISTORY_KEY, null)
                ?: return RecentSet(capacity) { it.trackId }
        val tracks = gson.fromJson(json, Array<Track>::class.java).toList()
        return RecentSet.fromList(tracks, capacity) { it.trackId }
    }

    override fun addTrack(track: Track) {
        val recentTracks = getHistory()
        recentTracks.put(track)
        val json = gson.toJson(recentTracks.toList())
        sharedPreferences.edit { putString(TRACKS_HISTORY_KEY, json) }
    }

    override fun clearHistory() {
        sharedPreferences.edit { remove(TRACKS_HISTORY_KEY) }
    }

    companion object {
        private const val TRACKS_HISTORY_KEY = "tracks_history"
        private const val HISTORY_CAPACITY = 10
    }
}
