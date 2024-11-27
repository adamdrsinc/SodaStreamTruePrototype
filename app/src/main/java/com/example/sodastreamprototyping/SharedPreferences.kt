package com.example.sodastreamprototyping

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private const val PREFS_NAME = "UserPreferences"
    private const val IS_LOGGED_IN = "isLoggedIn"

    // Method to log in and store tokens
    fun login(sharedPreferences: SharedPreferences, access_token: String, refresh_token: String) {
//        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(IS_LOGGED_IN, true)
            .putString("AccessToken", access_token)
            .putString("RefreshToken", refresh_token)
            .apply()
    }

    // Method to log out and clear tokens
    fun logout(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(IS_LOGGED_IN, false)
            .remove("AccessToken")
            .remove("RefreshToken")
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun getAccessToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("AccessToken", null)
    }

    fun getRefreshToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("RefreshToken", null)
    }

    fun setAccessToken(context: Context, accessToken: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("AccessToken", accessToken)
            .apply()
    }
}
