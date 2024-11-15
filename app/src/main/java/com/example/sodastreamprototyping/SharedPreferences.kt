package com.example.sodastreamprototyping

import android.content.Context

object UserPreferences {
    private const val PREFS_NAME = "UserPreferences"
    private const val IS_LOGGED_IN = "isLoggedIn"

    fun setLoggedIn(context: Context, loggedIn: Boolean, access_token: String, refresh_token: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN, loggedIn).apply()
        sharedPreferences.edit().putString("AccessToken", acces_token).apply()
        sharedPreferences.edit().putString("RefreshToken", refresh_token).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun getAccessToken(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("AccessToken", "")
    }

    fun getRefreshToken(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("RefreshToken", "")
    }
}
