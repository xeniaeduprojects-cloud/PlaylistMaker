package com.praktikum.playlistmaker.search.domain

import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepository
import com.praktikum.playlistmaker.search.domain.model.Track

class AddTrackToHistoryUseCase(
    private val trackHistoryRepository: TrackHistoryRepository,
) {
    operator fun invoke(track: Track) {
        trackHistoryRepository.addTrack(track)
    }
}
