package com.praktikum.playlistmaker.search.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.playlistmaker.databinding.ActivitySearchBinding
import com.praktikum.playlistmaker.player.ui.PlayerActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
    }

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var searchTrackAdapter: TrackAdapter
    private lateinit var historyTrackAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.llMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            binding.llScrollContent.setPadding(0, 0, 0, ime.bottom)

            WindowInsetsCompat.CONSUMED
        }

        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }

        searchTrackAdapter =
            TrackAdapter { track ->
                viewModel.onTrackClick(track)
            }

        historyTrackAdapter =
            TrackAdapter { track ->
                viewModel.onTrackClick(track)
            }

        binding.rvTracks.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchTrackAdapter
        }

        binding.etSearch.addTextChangedListener(
            afterTextChanged = { s ->
                viewModel.onSearchQueryRequested(s.toString())
            },
        )

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.onSearchTextEditInFocus()
            }
        }

        binding.imgSearchClear.setOnClickListener {
            binding.etSearch.text.clear()
            viewModel.onClearButtonClicked()

            WindowCompat
                .getInsetsController(window, binding.etSearch)
                .hide(WindowInsetsCompat.Type.ime())
        }

        binding.btnRefresh.setOnClickListener {
            viewModel.onSearchQueryRequested(binding.etSearch.text.toString())
        }

        binding.btnClearHistory.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            renderState(state)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigateToPlayer.collect { track ->
                    startActivity(PlayerActivity.createIntent(this@SearchActivity, track))
                }
            }
        }
    }

    private fun renderState(state: SearchUiState) {
        binding.imgSearchClear.isVisible = state.searchQuery.isNotEmpty()
        binding.tvNothingFound.isVisible = state is SearchUiState.SearchEmpty
        binding.llNoConnection.isVisible = state is SearchUiState.Error
        binding.pbLoading.isVisible = state is SearchUiState.Loading

        val hasHistory = state is SearchUiState.HistoryContent && state.tracks.isNotEmpty()
        binding.tvSearchHistoryTitle.isVisible = hasHistory
        binding.btnClearHistory.isVisible = hasHistory

        renderTracks(state)
    }

    private fun renderTracks(state: SearchUiState) {
        val (adapter, tracks) =
            when (state) {
                is SearchUiState.HistoryContent -> historyTrackAdapter to state.tracks
                is SearchUiState.SearchContent -> searchTrackAdapter to state.tracks
                is SearchUiState.SearchEmpty,
                is SearchUiState.Error,
                is SearchUiState.Loading,
                -> searchTrackAdapter to emptyList()
            }

        if (binding.rvTracks.adapter != adapter) {
            binding.rvTracks.adapter = adapter
        }
        adapter.updateTracks(tracks)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, viewModel.uiState.value?.searchQuery ?: "")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        binding.etSearch.setText(searchQuery)
        viewModel.restoreSearchQuery(searchQuery)
    }
}
