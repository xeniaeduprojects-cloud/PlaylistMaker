package com.praktikum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btnSearch)
        btnSearch.setOnClickListener {
            Toast.makeText(this@MainActivity, "Search button", Toast.LENGTH_SHORT).show()
        }

        val btnMediaLibrary = findViewById<Button>(R.id.btnMediaLibrary)
        btnMediaLibrary.setOnClickListener {
            Toast.makeText(this@MainActivity, "Media library button", Toast.LENGTH_SHORT).show()
        }

        val btnSettings = findViewById<Button>(R.id.btnSettings)
        val btnSettingsClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Settings button", Toast.LENGTH_SHORT).show()
            }
        }
        btnSettings.setOnClickListener(btnSettingsClickListener)
    }
}