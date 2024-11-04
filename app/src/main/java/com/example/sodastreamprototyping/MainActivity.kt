package com.example.sodastreamprototyping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Navigation()
            }
        }
    }
}

/*
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "sign_in") {
        composable("sign_in") {
            SignInScreen(
                onSignUpClick = { navController.navigate("sign_up") },
                onSignInSuccess = { navController.navigate("home") }
            )
        }
        composable("sign_up") {
            SignUpScreen(
                onSignInClick = { navController.popBackStack() }
            )
        }
        composable("home") {
            Home()
        }

    }
}*/
