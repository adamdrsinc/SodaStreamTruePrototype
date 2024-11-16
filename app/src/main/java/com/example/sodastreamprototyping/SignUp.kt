package com.example.sodastreamprototyping

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.practice.ApiRequestHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun SignUpScreen(onSignInClick: () -> Unit) {
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    errorMessage = "Please fill out all fields"
                } else {
                    errorMessage = null
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            ApiRequestHelper.makeSignUpRequest(
                                context = context,
                                firstname = firstname,
                                lastname = lastname,
                                username = username,
                                email = email,
                                password = password,
                                onSuccess = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        Log.d("SIGNUP_SUCCESS", "User registered successfully")
                                        onSignInClick()
                                    }
                                },
                                onError = { error ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        errorMessage = when (error) {
                                            "NetworkError" -> "Network error. Please check your connection."
                                            "EmailInUse" -> "The email is already registered. Try logging in."
                                            "UsernameTaken" -> "Username is taken. Please choose another."
                                            else -> "An unexpected error occurred: $error"
                                        }
                                        Log.e("SIGNUP_ERROR", error)
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            CoroutineScope(Dispatchers.Main).launch {
                                errorMessage = "An error occurred: ${e.localizedMessage}"
                                Log.e("SIGNUP_EXCEPTION", e.toString())
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onSignInClick) {
            Text(text = "Already have an account? Sign In")
        }
    }
}
