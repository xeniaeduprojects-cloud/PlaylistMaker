package com.praktikum.playlistmaker.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.praktikum.playlistmaker.R
import com.praktikum.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.llMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbarSettings.setNavigationOnClickListener {
            finish()
        }

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onDarkModeToggled(isChecked)
        }

        binding.tvShare.setOnClickListener {
            viewModel.onShareClicked()
        }

        binding.tvSupport.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.tvUserAgreement.setOnClickListener {
            viewModel.onUserAgreementClicked()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            binding.switchDarkTheme.isChecked = state.isDarkModeEnabled
        }

        viewModel.navigationEvent.observe(this) { event ->
            event?.let {
                handleNavigationEvent(it)
                viewModel.onNavigationEventHandled()
            }
        }
    }

    private fun handleNavigationEvent(event: SettingsNavigationEvent) {
        when (event) {
            is SettingsNavigationEvent.Share -> {
                val message = getString(event.messageResId)
                val intent =
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                val title = getString(R.string.share_chooser_title)
                startActivity(Intent.createChooser(intent, title))
            }

            is SettingsNavigationEvent.SendEmail -> {
                val subject = getString(event.subjectResId)
                val text = getString(event.textResId)
                val intent =
                    Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:".toUri()
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(event.email))
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                startActivity(intent)
            }

            is SettingsNavigationEvent.OpenUrl -> {
                val intent = Intent(Intent.ACTION_VIEW, event.url.toUri())
                startActivity(intent)
            }

            is SettingsNavigationEvent.ApplyTheme -> {
                AppCompatDelegate.setDefaultNightMode(
                    if (event.isDarkMode) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_NO
                    },
                )
            }
        }
    }
}
