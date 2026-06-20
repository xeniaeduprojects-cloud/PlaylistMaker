package com.praktikum.playlistmaker.player.domain

import com.praktikum.playlistmaker.player.data.media.AudioPlayerManager

class SeekToPositionUseCase(
    private val audioPlayerManager: AudioPlayerManager,
) {
    operator fun invoke(positionMs: Long) {
        audioPlayerManager.seekTo(positionMs)
    }
}
