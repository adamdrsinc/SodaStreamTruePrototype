package com.example.sodastreamprototyping.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.practice.ApiRequestHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.isEmpty

@HiltViewModel
class SignInViewModel @Inject constructor(
    val requestHelper: ApiRequestHelper,
) : ViewModel() {
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
            requestHelper.makeLoginRequest(username = username.value,
                password = password.value,
                onSuccess = { response ->
                    try {
                        _signInSuccess.value = true
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

    fun signUp() {
        if (firstname.value.isEmpty() || lastname.value.isEmpty() || username.value.isEmpty() || email.value.isEmpty() || password.value.isEmpty()) {
            errorMessage.value = "Please fill out all fields"
        } else {
            errorMessage.value = null

            requestHelper.makeSignUpRequest(
                firstname = firstname.value,
                lastname = lastname.value,
                username = username.value,
                email = email.value,
                password = password.value,
                onSuccess = {
                    _signInSuccess.value = true
                },
                onError = { error ->
                    errorMessage.value = error
                })

        }
    }
}