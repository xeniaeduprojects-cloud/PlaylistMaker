package com.praktikum.playlistmaker.app

import android.app.Application
import android.util.Log

class PlaylistMakerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupGlobalExceptionHandler()
    }

    private fun setupGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UncaughtException", "Thread: ${thread.name}", throwable)
            Log.e("UncaughtException", "Exception: ${throwable.message}")
            Log.e("UncaughtException", "Stack trace:", throwable)

            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
