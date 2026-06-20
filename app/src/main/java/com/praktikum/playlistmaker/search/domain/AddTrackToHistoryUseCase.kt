package com.praktikum.playlistmaker.search.domain

import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepository

class AddTrackToHistoryUseCase(
    private val trackHistoryRepository: TrackHistoryRepository,
) {
    operator fun invoke(track: Track) {
        trackHistoryRepository.addTrack(track)
    }
}
