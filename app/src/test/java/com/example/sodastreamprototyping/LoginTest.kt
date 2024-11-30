package com.example.sodastreamprototyping


import com.example.practice.ApiRequestHelper
import com.example.sodastreamprototyping.viewModel.SignInViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before

import org.json.JSONObject
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock


class LoginTest {
    lateinit var apiRequestHelper : ApiRequestHelper

    @Before
    fun setUpMocks(){
        val successResponse = JSONObject().apply { put("access_token", "123"); put("refresh_token", "123")}
        val error = "sample error"

        apiRequestHelper = mock<ApiRequestHelper>{
            on{makeLoginRequest(eq("success"), eq("123"), any(), any())} doAnswer { invocation ->
                @Suppress("UNCHECKED_CAST")
                val onSuccess = invocation.arguments[2] as Function1<JSONObject, Unit>
                onSuccess(successResponse)
            }

            on{makeLoginRequest(eq("fail"), any(), any(), any())} doAnswer {invocation ->
                @Suppress("UNCHECKED_CAST")
                val onFail = invocation.arguments[3] as Function1<String, Unit>
                onFail(error)
            }
        }

    }

    @Test
    fun `sign in success`(){
        val viewModel = SignInViewModel(apiRequestHelper)
        viewModel.username.value = "success"
        viewModel.password.value = "123"
        viewModel.signIn()
        assertTrue(viewModel.signInSuccess.value)
    }

    @Test
    fun `sign in failure shows error`(){
        val viewModel = SignInViewModel(apiRequestHelper)
        viewModel.username.value = "fail"
        viewModel.password.value = "123"
        viewModel.signIn()
        assertEquals("sample error", viewModel.errorMessage.value)
        assertFalse(viewModel.signInSuccess.value)
    }

    @Test
    fun `sign in with empty field gives error`(){
        val viewModel = SignInViewModel(apiRequestHelper)
        viewModel.username.value = "fail"
        viewModel.password.value = ""
        viewModel.signIn()
        assertEquals("Please fill out all fields", viewModel.errorMessage.value)
        assertFalse(viewModel.signInSuccess.value)
    }


}