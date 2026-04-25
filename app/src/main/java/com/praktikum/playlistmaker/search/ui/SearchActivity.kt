package com.praktikum.playlistmaker.search.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.playlistmaker.databinding.ActivitySearchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
    }

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }

        trackAdapter =
            TrackAdapter { track ->
                WindowCompat
                    .getInsetsController(window, binding.searchEditText)
                    .hide(WindowInsetsCompat.Type.ime())
                Toast.makeText(this, "${track.trackName} clicked", Toast.LENGTH_SHORT).show()
            }

        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = trackAdapter
        }

        binding.searchEditText.addTextChangedListener(
            afterTextChanged = { s ->
                viewModel.onSearchQueryRequested(s.toString())
            },
        )

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 1. create DS for keeping search history
                // 2. get history from SP -> DS
                // 3. if not empty -> show history
                // 4. if empty -> hide history
                // TODO("AND if empty, show the history")
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

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SearchUiState) {
        binding.searchClearButton.isVisible =
            when (state) {
                is SearchUiState.Idle -> false
                else -> true
            }
        binding.nothingFoundText.isVisible = state is SearchUiState.SearchEmpty
        binding.noConnectionLayout.isVisible = state is SearchUiState.Error
        binding.searchHistoryTitle.isVisible = state is SearchUiState.HistoryContent
        binding.clearHistoryButton.isVisible = state is SearchUiState.HistoryContent
        trackAdapter.updateTracks(
            when (state) {
                is SearchUiState.SearchContent -> state.tracks
                is SearchUiState.Loading -> state.tracks
                is SearchUiState.HistoryContent -> state.tracks
                else -> emptyList()
            },
        )
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
