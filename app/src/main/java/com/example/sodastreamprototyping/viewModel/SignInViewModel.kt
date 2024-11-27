package com.example.sodastreamprototyping.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.RequestQueue
import com.example.practice.ApiRequestHelper
import com.example.sodastreamprototyping.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.text.isEmpty

@HiltViewModel
class SignInViewModel @Inject constructor(val requestQueue: RequestQueue, val sharedPreferences: SharedPreferences) :
    ViewModel() {
    val firstname = mutableStateOf("")
    val lastname = mutableStateOf("")
    val username = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val errorMessage = mutableStateOf<String?>("")

    private val _signInSuccess = MutableStateFlow(false)
    val signInSuccess = _signInSuccess.asStateFlow()

    fun signIn() {
        if (username.value.isEmpty() || password.value.isEmpty()) {
            errorMessage.value = "Please fill out all fields"
        } else {
            errorMessage.value = null
            ApiRequestHelper.makeLoginRequest(requestQueue,
                username = username.value,
                password = password.value,
                onSuccess = { response ->
                    try {
                        val accessToken = response.getString("access_token")
                        Log.d("ACCESS_TOKEN", "Access Token: $accessToken")
                        val refreshToken = response.getString("refresh_token")
                        Log.d("REFRESH_TOKEN", "Refresh Token: $refreshToken")
                        UserPreferences.login(sharedPreferences, accessToken, refreshToken)
                        _signInSuccess.value = true
                        //TODO update a value or something
//                        onSignInSuccess()
                    } catch (e: Exception) {
                        Log.e("LOGIN_ERROR", e.toString())
                        errorMessage.value = "Failed to parse tokens"
                    }
                },
                onError = { error ->
                    errorMessage.value = error
                })
        }
    }
}