package com.example.hiline

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Disable dark mode for the entire application
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}