package com.example.reduxtodo.model

import com.example.reduxtodo.App

private const val STATE_KEY = "state"

object Database {
    fun writeJsonState(json: String) {
        App.prefs.edit().putString(STATE_KEY, json).apply()
    }

    fun readJsonState(): String? {
        return App.prefs.getString(STATE_KEY, null)
    }
}