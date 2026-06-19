package com.praktikum.playlistmaker.search.domain

import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepository
import com.praktikum.playlistmaker.search.domain.model.Track

class GetSearchHistoryUseCase(
    private val trackHistoryRepository: TrackHistoryRepository,
) {
    operator fun invoke(): List<Track> = trackHistoryRepository.getHistory().toList()
}
