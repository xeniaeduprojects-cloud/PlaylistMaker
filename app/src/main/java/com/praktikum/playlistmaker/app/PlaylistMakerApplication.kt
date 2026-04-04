package com.praktikum.playlistmaker.app

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.praktikum.playlistmaker.BuildConfig

class PlaylistMakerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
        setupGlobalExceptionHandler()
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
