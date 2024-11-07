package com.example.sodastreamprototyping

import android.content.Context

object UserPreferences {
    private const val PREFS_NAME = "UserPreferences"
    private const val IS_LOGGED_IN = "isLoggedIn"

    fun setLoggedIn(context: Context, loggedIn: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN, loggedIn).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }
}