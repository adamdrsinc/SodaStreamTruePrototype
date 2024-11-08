package com.example.sodastreamprototyping

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SodaSensationsApplication: Application() {
    init{
        Log.d("app", "working")
    }
}