package com.praktikum.playlistmaker.search.domain

import com.praktikum.playlistmaker.search.data.model.Track
import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepository

class GetSearchHistoryUseCase(
    private val trackHistoryRepository: TrackHistoryRepository,
) {
    operator fun invoke(): List<Track> = trackHistoryRepository.getHistory().toList()
}
