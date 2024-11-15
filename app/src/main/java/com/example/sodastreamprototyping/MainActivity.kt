package com.example.sodastreamprototyping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.practice.ApiRequestHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MaterialTheme {
                val isLoggedIn = UserPreferences.isLoggedIn(this@MainActivity)
                val startDestination: String
                if isLoggedIn {
                    val refreshToken = UserPreferences.getRefreshToken(this@MainActivity)

                    val accesToken = "" // Talk to Jakob about how to use the refresh token to get a new access token

                    // Pass access token so all requests can use it for authentication
                    ApiRequestHelper.retrieveAllNeededData(this, accessToken)
                    startDestination = Screen.Home.route 
                } else {
                    startDestination = Screen.SignIn.route
                }
                Navigation(startDestination)
            }
        }
    }
}



