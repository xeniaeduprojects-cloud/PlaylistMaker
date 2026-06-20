package com.praktikum.playlistmaker.player.domain

import com.praktikum.playlistmaker.player.data.media.AudioPlayerManager
import com.praktikum.playlistmaker.player.data.media.PlayerState

class PreparePlayerUseCase(
    private val audioPlayerManager: AudioPlayerManager,
) {
    operator fun invoke(
        url: String,
        onStateChanged: (PlayerState) -> Unit,
    ) {
        audioPlayerManager.prepare(url, onStateChanged)
    }
}
