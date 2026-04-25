package com.praktikum.playlistmaker.search.data.repository

import com.praktikum.playlistmaker.search.data.model.Track

interface TrackHistoryRepository {
    fun getHistory(): List<Track>
}
