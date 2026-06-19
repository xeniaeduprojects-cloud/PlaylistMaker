package com.praktikum.playlistmaker.app

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.praktikum.playlistmaker.BuildConfig
import com.praktikum.playlistmaker.player.di.playerModule
import com.praktikum.playlistmaker.search.di.searchModule
import com.praktikum.playlistmaker.settings.di.settingsModule
import com.praktikum.playlistmaker.settings.domain.GetDarkModeUseCase
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
        setupKoin()
        applyThemePreference()
    }

    private fun setupKoin() {
        startKoin {
            androidLogger()
            androidContext(this@PlaylistMakerApplication)
            modules(settingsModule, searchModule, playerModule)
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
        val getDarkModeUseCase =
            org.koin.java.KoinJavaComponent
                .get<GetDarkModeUseCase>(GetDarkModeUseCase::class.java)
        val isDarkMode = getDarkModeUseCase()
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
}
