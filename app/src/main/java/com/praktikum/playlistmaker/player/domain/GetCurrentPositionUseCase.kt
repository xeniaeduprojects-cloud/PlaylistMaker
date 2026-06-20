package com.praktikum.playlistmaker.player.domain

import com.praktikum.playlistmaker.player.data.media.AudioPlayerManager

class GetCurrentPositionUseCase(
    private val audioPlayerManager: AudioPlayerManager,
) {
    operator fun invoke(): Long = audioPlayerManager.getCurrentPosition()
}
