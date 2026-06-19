package com.praktikum.playlistmaker.player.domain

import com.praktikum.playlistmaker.player.data.media.AudioPlayerManager

class ReleasePlayerUseCase(
    private val audioPlayerManager: AudioPlayerManager,
) {
    operator fun invoke() {
        audioPlayerManager.release()
    }
}
