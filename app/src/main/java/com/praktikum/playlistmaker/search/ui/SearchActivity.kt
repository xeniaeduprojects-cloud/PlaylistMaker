package com.praktikum.playlistmaker.search.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.playlistmaker.databinding.ActivitySearchBinding
import com.praktikum.playlistmaker.player.ui.PlayerActivity
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            binding.scrollContent.setPadding(0, 0, 0, ime.bottom)

            WindowInsetsCompat.CONSUMED
        }

        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }

        searchTrackAdapter =
            TrackAdapter { track ->
                viewModel.onTrackClick(track)
            }

        historyTrackAdapter = TrackAdapter()

        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchTrackAdapter
        }

        binding.searchEditText.addTextChangedListener(
            afterTextChanged = { s ->
                viewModel.onSearchQueryRequested(s.toString())
            },
        )

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.onSearchTextEditInFocus()
            }
        }

        binding.searchClearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            viewModel.onClearButtonClicked()

            WindowCompat
                .getInsetsController(window, binding.searchEditText)
                .hide(WindowInsetsCompat.Type.ime())
        }

        binding.refreshButton.setOnClickListener {
            viewModel.onSearchQueryRequested(binding.searchEditText.text.toString())
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            renderState(state)
        }

        viewModel.navigateToPlayer.observe(this) { track ->
            val intent = PlayerActivity.createIntent(context = this, track = track)
            startActivity(intent)
        }
    }

    private fun renderState(state: SearchUiState) {
        binding.searchClearButton.isVisible = state.searchQuery.isNotEmpty()
        binding.nothingFoundText.isVisible = state is SearchUiState.SearchEmpty
        binding.noConnectionLayout.isVisible = state is SearchUiState.Error

        val hasHistory = state is SearchUiState.HistoryContent && state.tracks.isNotEmpty()
        binding.searchHistoryTitle.isVisible = hasHistory
        binding.clearHistoryButton.isVisible = hasHistory

        renderTracks(state)
    }

    private fun renderTracks(state: SearchUiState) {
        val (adapter, tracks) =
            when (state) {
                is SearchUiState.HistoryContent -> historyTrackAdapter to state.tracks
                is SearchUiState.Loading -> searchTrackAdapter to state.tracks
                is SearchUiState.SearchContent -> searchTrackAdapter to state.tracks
                is SearchUiState.SearchEmpty,
                is SearchUiState.Error,
                -> searchTrackAdapter to emptyList()
            }

        if (binding.tracksRecyclerView.adapter != adapter) {
            binding.tracksRecyclerView.adapter = adapter
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
        binding.searchEditText.setText(searchQuery)
        viewModel.restoreSearchQuery(searchQuery)
    }
}
