package com.praktikum.playlistmaker.player.data.media

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayerManagerImpl(
    context: Context,
) : AudioPlayerManager {
    companion object {
        private const val TAG = "AudioPlayerManager"
    }

    private val player = ExoPlayer.Builder(context).build()
    private var stateCallback: ((PlayerState) -> Unit)? = null

    private val listener =
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val stateName =
                    when (playbackState) {
                        Player.STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        Player.STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN"
                    }
                Log.d(TAG, "onPlaybackStateChanged: $stateName, isPlaying=${player.isPlaying}")

                val state =
                    when (playbackState) {
                        Player.STATE_IDLE -> PlayerState.IDLE
                        Player.STATE_BUFFERING -> PlayerState.BUFFERING
                        Player.STATE_READY -> if (player.isPlaying) PlayerState.PLAYING else PlayerState.READY
                        Player.STATE_ENDED -> PlayerState.ENDED
                        else -> PlayerState.IDLE
                    }
                Log.d(TAG, "Emitting state: $state")
                stateCallback?.invoke(state)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(TAG, "onIsPlayingChanged: $isPlaying, playbackState=${player.playbackState}")
                if (isPlaying) {
                    stateCallback?.invoke(PlayerState.PLAYING)
                } else if (player.playbackState == Player.STATE_READY) {
                    stateCallback?.invoke(PlayerState.PAUSED)
                }
            }
        }

    override fun prepare(
        url: String,
        onStateChanged: (PlayerState) -> Unit,
    ) {
        Log.d(TAG, "prepare: $url")
        stateCallback = onStateChanged
        player.addListener(listener)
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    override fun play() {
        Log.d(TAG, "play() called")
        player.play()
    }

    override fun pause() {
        Log.d(TAG, "pause() called")
        player.pause()
    }

    override fun release() {
        player.removeListener(listener)
        player.release()
        stateCallback = null
    }

    override fun isPlaying(): Boolean = player.isPlaying

    override fun getCurrentPosition(): Long = player.currentPosition

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }
}
