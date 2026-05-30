package com.praktikum.playlistmaker.player.data.media

interface AudioPlayerManager {
    fun prepare(
        url: String,
        onStateChanged: (PlayerState) -> Unit,
    )

    fun play()

    fun pause()

    fun release()

    fun isPlaying(): Boolean

    fun getCurrentPosition(): Long

    fun seekTo(positionMs: Long)
}

enum class PlayerState {
    IDLE,
    BUFFERING,
    READY,
    PLAYING,
    PAUSED,
    ENDED,
    ERROR,
}
