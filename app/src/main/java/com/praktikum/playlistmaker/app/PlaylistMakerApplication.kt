package com.praktikum.playlistmaker.app

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.praktikum.playlistmaker.BuildConfig
import com.praktikum.playlistmaker.search.di.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PlaylistMakerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        setupGlobalExceptionHandler()
        applyThemePreference()
        setupKoin()
    }

    private fun setupKoin() {
        startKoin {
            androidLogger()
            androidContext(this@PlaylistMakerApplication)
            modules(searchModule)
        }
    }

    private fun enableStrictMode() {
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy
                .Builder()
                .detectLeakedClosableObjects()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .build(),
        )
    }

    private fun applyThemePreference() {
        val sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            },
        )
    }

    private fun setupGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UncaughtException", "Thread: ${thread.name}", throwable)

            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "playlist_maker_preferences"
        private const val KEY_DARK_MODE = "dark_mode"
    }
}
