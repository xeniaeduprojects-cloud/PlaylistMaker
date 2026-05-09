package com.praktikum.playlistmaker.player.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
                val cornerRadiusPx = resources.getDimensionPixelSize(R.dimen.player_album_art_corner_radius)

                Glide
                    .with(this)
                    .load(state.artworkUrl)
                    .placeholder(R.drawable.album_placeholder)
                    .error(R.drawable.album_placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(cornerRadiusPx))
                    .into(binding.imgAlbumArt)

                binding.tvTrackTitle.text = state.trackName
                binding.tvArtistName.text = state.artistName
                binding.tvDuration.text = state.duration

                setMetaInfoRowVisibility(state.album, binding.labelAlbum, binding.tvAlbum)
                setMetaInfoRowVisibility(state.year, binding.labelYear, binding.tvYear)
                setMetaInfoRowVisibility(state.genre, binding.labelGenre, binding.tvGenre)
                setMetaInfoRowVisibility(state.country, binding.labelCountry, binding.tvCountry)
            }
        }
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
