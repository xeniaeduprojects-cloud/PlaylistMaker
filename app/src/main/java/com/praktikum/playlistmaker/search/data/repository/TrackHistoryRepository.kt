package com.praktikum.playlistmaker.search.data.repository

import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.util.RecentSet

interface TrackHistoryRepository {
    fun getHistory(): RecentSet<Long, Track>

    fun addTrack(track: Track)

    fun clearHistory()
}
