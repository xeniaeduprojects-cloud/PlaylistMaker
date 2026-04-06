package com.praktikum.playlistmaker.app

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.praktikum.playlistmaker.BuildConfig
import com.praktikum.playlistmaker.search.di.searchUiModule
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
    }

    private fun setupKoin() {
        startKoin {
            androidLogger()
            androidContext(this@PlaylistMakerApplication)
            modules(searchUiModule)
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
