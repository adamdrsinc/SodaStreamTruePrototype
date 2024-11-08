package com.example.sodastreamprototyping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val isLoggedIn = UserPreferences.isLoggedIn(this@MainActivity)
                val startDestination = if (isLoggedIn) Screen.Home.route else Screen.SignIn.route
                Navigation(startDestination)
            }
        }
    }
}

