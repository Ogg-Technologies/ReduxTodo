package com.example.reduxtodo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        contextOrNull = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var contextOrNull: Context? = null
            private set

        val context : Context
            get() = contextOrNull!!

        val prefs by lazy { context.getSharedPreferences("main", Context.MODE_PRIVATE) }
    }
}