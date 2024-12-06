package com.example.sodastreamprototyping

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import com.example.practice.ApiRequestHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Use Compose state to hold the start destination
                var startDestination by remember { mutableStateOf<String?>(null) }

                // Side-effect to determine the start destination
                LaunchedEffect(Unit) {
//                    val isLoggedIn = UserPreferences.isLoggedIn(this@MainActivity)
                    if (false) {
                        val refreshToken = UserPreferences.getRefreshToken(this@MainActivity)
                        val accessToken = UserPreferences.getAccessToken(this@MainActivity)

                        if (accessToken == null) {
                            // Access token is missing, attempt to refresh
                            if (refreshToken != null) {
                                ApiRequestHelper.refreshAccessToken(
                                    context = this@MainActivity,
                                    refreshToken = refreshToken,
                                    onSuccess = {
                                        // Proceed after refreshing token
                                        ApiRequestHelper.retrieveAllNeededData(this@MainActivity)
                                        startDestination = Screen.Home.route
                                    },
                                    onError = { error ->
                                        // Handle error, navigate to login screen
                                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                                        startDestination = Screen.SignIn.route
                                    }
                                )
                            } else {
                                // No tokens available, redirect to sign-in
                                startDestination = Screen.SignIn.route
                            }
                        } else {
                            // Access token exists, proceed
                            ApiRequestHelper.retrieveAllNeededData(this@MainActivity)
                            startDestination = Screen.Home.route
                        }
                    } else {
                        startDestination = Screen.SignIn.route
                    }
                }

                // Display the appropriate screen based on startDestination
                if (startDestination != null) {
                    Navigation(startDestination!!)
                } else {
                    // Show a loading indicator while determining startDestination
                    CircularProgressIndicator()
                }
            }
        }
    }
}
