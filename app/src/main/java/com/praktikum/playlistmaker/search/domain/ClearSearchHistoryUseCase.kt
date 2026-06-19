package com.praktikum.playlistmaker.search.domain

import com.praktikum.playlistmaker.search.data.repository.TrackHistoryRepository

class ClearSearchHistoryUseCase(
    private val trackHistoryRepository: TrackHistoryRepository,
) {
    operator fun invoke() {
        trackHistoryRepository.clearHistory()
    }
}
