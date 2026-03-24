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
import com.praktikum.playlistmaker.search.data.model.Track

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
    }

    private lateinit var binding: ActivitySearchBinding
    private var searchQuery = ""
    private val tracks = mutableListOf<Track>()
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

        tracks.addAll(
            listOf(
                Track(
                    trackName = "Smells Like Teen Spirit",
                    artistName = "Nirvana",
                    trackTime = "5:01",
                    artworkUrl100 =
                        "https://is5-ssl.mzstatic.com/image/thumb/Music115/v" +
                            "4/7b/58/c2/7b58c23a-f5a5-5a7c-b9f5-8d5d8f5d5f5d/source/100x100bb.jpg",
                ),
                Track(
                    trackName = "Billie Jean",
                    artistName = "Michael Jackson",
                    trackTime = "4:53",
                    artworkUrl100 =
                        "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4" +
                            "/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/source/100x100bb.jpg",
                ),
            ),
        )

        trackAdapter =
            TrackAdapter(tracks) { track ->
            }

        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = trackAdapter
        }

        binding.searchEditText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                binding.searchClearButton.isVisible = !s.isNullOrEmpty()
            },
            afterTextChanged = { s ->
                searchQuery = s.toString()
            },
        )

        binding.searchClearButton.setOnClickListener {
            binding.searchEditText.text.clear()

            WindowCompat
                .getInsetsController(window, binding.searchEditText)
                .hide(WindowInsetsCompat.Type.ime())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        binding.searchEditText.setText(searchQuery)
    }
}
