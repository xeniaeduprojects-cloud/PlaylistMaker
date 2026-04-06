package com.praktikum.playlistmaker.search.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.praktikum.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
    }

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
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
        binding.searchClearButton.isVisible = state.showClearButton
        binding.nothingFoundText.isVisible = state.showNoResults
        binding.noConnectionLayout.isVisible = state.showNoConnection
        trackAdapter.updateTracks(state.tracks)
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
