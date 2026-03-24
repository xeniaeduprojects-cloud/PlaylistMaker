package com.praktikum.playlistmaker.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.praktikum.playlistmaker.databinding.ActivityMainBinding
import com.praktikum.playlistmaker.medialibrary.MediaLibraryActivity
import com.praktikum.playlistmaker.search.ui.SearchActivity
import com.praktikum.playlistmaker.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSearchClickListener: View.OnClickListener =
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val displayIntent = Intent(this@MainActivity, SearchActivity::class.java)
                    startActivity(displayIntent)
                }
            }
        binding.btnSearch.setOnClickListener(btnSearchClickListener)

        binding.btnMediaLibrary.setOnClickListener {
            val displayIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(displayIntent)
        }

        binding.btnSettings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
    }
}
