package com.praktikum.playlistmaker.player.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.databinding.ActivityPlayerBinding
import com.praktikum.playlistmaker.search.data.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(getTrackFromIntent())
    }

    private val url =
        "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/" +
            "v4/ac/c7/d1/acc7d13f-6634-495f-caf6-491eccb505e8/" +
            "mzaf_4002676889906514534.plus.aac.p.m4a"

    private fun getTrackFromIntent(): Track? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TRACK)
        }

    companion object {
        private const val EXTRA_TRACK = "EXTRA_TRACK"
        private const val TAG = "PlayerActivity"

        fun createIntent(
            context: Context,
            track: Track,
        ): Intent =
            Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK, track)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnPlayPause.setOnClickListener {
            Log.d(TAG, "Play/Pause button clicked")
            viewModel.onPlayPauseClick()
        }

        observeViewModel()
        Log.d(TAG, "Preparing player with URL: $url")
        viewModel.prepare(url)
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            Log.d(TAG, "UI State changed: $state")
            renderState(state)
        }

        viewModel.playbackState.observe(this) { playbackState ->
            Log.d(TAG, "Playback State changed: $playbackState")
            when (playbackState) {
                is PlaybackState.Playing -> {
                    Log.d(TAG, "State: Playing")
                }
                is PlaybackState.Paused -> {
                    Log.d(TAG, "State: Paused")
                }
                is PlaybackState.Buffering -> {
                    Log.d(TAG, "State: Buffering")
                }
                is PlaybackState.Idle -> {
                    Log.d(TAG, "State: Idle")
                }
            }
        }
    }

    private fun renderState(state: PlayerUiState) {
        when (state) {
            is PlayerUiState.Playing -> {
                renderContent(state.content)
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause_button)
                binding.tvPlaybackTime.text = formatTime(state.currentPositionSeconds)
            }
            is PlayerUiState.Paused -> {
                renderContent(state.content)
                binding.btnPlayPause.setImageResource(R.drawable.ic_play_button)
                binding.tvPlaybackTime.text = formatTime(state.currentPositionSeconds)
            }
        }
    }

    @Suppress("MagicNumber")
    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format(Locale.ROOT, "%d:%02d", minutes, secs)
    }

    private fun renderContent(content: TrackContent) {
        val cornerRadiusPx = resources.getDimensionPixelSize(R.dimen.player_album_art_corner_radius)

        Glide
            .with(this)
            .load(content.artworkUrl)
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusPx))
            .into(binding.imgAlbumArt)

        binding.tvTrackTitle.text = content.trackName
        binding.tvArtistName.text = content.artistName
        binding.tvDuration.text = content.duration

        setMetaInfoRowVisibility(content.album, binding.labelAlbum, binding.tvAlbum)
        setMetaInfoRowVisibility(content.year, binding.labelYear, binding.tvYear)
        setMetaInfoRowVisibility(content.genre, binding.labelGenre, binding.tvGenre)
        setMetaInfoRowVisibility(content.country, binding.labelCountry, binding.tvCountry)
    }

    private fun setMetaInfoRowVisibility(
        value: String?,
        labelView: TextView,
        valueView: TextView,
    ) {
        val isVisible = value != null
        labelView.isVisible = isVisible
        valueView.isVisible = isVisible
        valueView.text = value
    }
}
