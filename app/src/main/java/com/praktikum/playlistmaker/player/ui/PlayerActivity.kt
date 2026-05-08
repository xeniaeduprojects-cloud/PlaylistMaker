package com.praktikum.playlistmaker.player.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.praktikum.playlistmaker.databinding.ActivityPlayerBinding
import com.praktikum.playlistmaker.search.data.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(getTrackFromIntent())
    }

    private fun getTrackFromIntent(): Track? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_TRACK)
        }

    companion object {
        private const val EXTRA_TRACK = "EXTRA_TRACK"

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

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlayerUiState) {
        when (state) {
            is PlayerUiState.Content -> {
                val unknown = getString(com.praktikum.playlistmaker.R.string.unknown)
                binding.tvTrackTitle.text = state.trackName
                binding.tvArtistName.text = state.artistName
                binding.tvDuration.text = state.duration
                binding.tvAlbum.text = state.album ?: unknown
                binding.tvYear.text = state.year ?: unknown
                binding.tvGenre.text = state.genre ?: unknown
                binding.tvCountry.text = state.country ?: unknown
            }
        }
    }
}
