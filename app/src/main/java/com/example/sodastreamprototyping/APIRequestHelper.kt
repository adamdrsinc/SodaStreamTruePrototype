package com.example.practice

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sodastreamprototyping.Repository
import com.example.sodastreamprototyping.UserPreferences
import org.json.JSONObject

class ApiRequestHelper {

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080"

        /*
         * Obtains all data for singletons etc. used in the app.
         */
        fun retrieveAllNeededData(context: Context) {
            fetchIngredients(
                context = context,
                onSuccess = { ingredients ->
                    Repository.drinkFlavorsFromDB = ingredients
                },
                onError = { error ->
                    Toast.makeText(context, "Error fetching ingredients: $error", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Function to handle login request
        fun makeLoginRequest(
            context: Context,
            username: String,
            password: String,
            onSuccess: (JSONObject) -> Unit,
            onError: (String) -> Unit
        ) {
            val url = "$BASE_URL/auth/authenticate"

            val jsonParams = JSONObject().apply {
                put("username", username)
                put("password", password)
            }

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonParams,
                { response ->
                    onSuccess(response)
                },
                { error ->
                    Log.e("API_ERROR", error.toString())
                    onError(error.message ?: "An error occurred during login")
                }
            )

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }

        // Function to handle sign-up request
        fun makeSignUpRequest(
            context: Context,
            firstname: String,
            lastname: String,
            username: String,
            email: String,
            password: String,
            onSuccess: (JSONObject) -> Unit,
            onError: (String) -> Unit
        ) {
            val url = "$BASE_URL/auth/register"

            val jsonParams = JSONObject().apply {
                put("firstname", firstname)
                put("lastname", lastname)
                put("username", username)
                put("email", email)
                put("password", password)
            }

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams,
                { response ->
                    onSuccess(response)
                },
                { error ->
                    if (error.networkResponse != null) {
                        val responseData = String(error.networkResponse.data, Charsets.UTF_8)
                        Log.e("API_ERROR", "Response Code: ${error.networkResponse.statusCode}, Error data: $responseData")
                    }
                    Log.e("API_ERROR", "Volley Error: $error")
                    onError(error.message ?: "An error occurred during sign-up")
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }

        // Function to refresh access token
        fun refreshAccessToken(
            context: Context,
            refreshToken: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            val url = "$BASE_URL/auth/refresh"

            val jsonParams = JSONObject().apply {
                put("refreshToken", refreshToken)
            }

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonParams,
                { response ->
                    try {
                        val newAccessToken = response.getString("access_token")
                        UserPreferences.setAccessToken(context, newAccessToken)
                        Log.d("REFRESHED_ACCESS_TOKEN", "New Access Token: $newAccessToken")
                        onSuccess()
                    } catch (e: Exception) {
                        Log.e("API_ERROR", e.toString())
                        onError("Failed to parse new access token")
                    }
                },
                { error ->
                    Log.e("API_ERROR", error.toString())
                    onError(error.message ?: "An error occurred while refreshing token")
                }
            )

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }

        // Function to fetch ingredients data
        fun fetchIngredients(
            context: Context,
            onSuccess: (List<String>) -> Unit,
            onError: (String) -> Unit
        ) {
            val url = "$BASE_URL/inventory/all"
            val accessToken = UserPreferences.getAccessToken(context)

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    try {
                        val syrups = mutableListOf<String>()
                        val ingredientsArray = response.getJSONArray("ingredients")
                        for (i in 0 until ingredientsArray.length()) {
                            val syrup = ingredientsArray.getString(i)
                            syrups.add(syrup)
                        }
                        onSuccess(syrups)
                    } catch (e: Exception) {
                        Log.e("API_ERROR", e.toString())
                        onError("Failed to parse ingredients")
                    }
                },
                { error ->
                    Log.e("API_ERROR", error.toString())
                    if (error.networkResponse?.statusCode == 401) {
                        // Access token might be expired, try refreshing
                        val refreshToken = UserPreferences.getRefreshToken(context)
                        if (refreshToken != null) {
                            refreshAccessToken(
                                context,
                                refreshToken,
                                onSuccess = {
                                    // Retry the request after refreshing token
                                    fetchIngredients(context, onSuccess, onError)
                                },
                                onError = { refreshError ->
                                    onError(refreshError)
                                }
                            )
                        } else {
                            onError("Authentication failed. Please log in again.")
                        }
                    } else {
                        onError(error.message ?: "An error occurred while fetching ingredients")
                    }
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $accessToken"
                    return headers
                }
            }

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }

        // Function to fetch payment intent
        fun fetchPaymentIntent(
            context: Context,
            amount: Double,
            currency: String,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit
        ) {
            val url = "$BASE_URL/payments/create-payment-intent"
            val jsonParams = JSONObject().apply {
                put("amount", (amount * 100).toInt()) // Convert dollars to cents
                put("currency", currency)
            }

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonParams,
                { response ->
                    try {
                        val clientSecret = response.getString("clientSecret")
                        onSuccess(clientSecret)
                    } catch (e: Exception) {
                        Log.e("API_ERROR", e.toString())
                        onError("Failed to parse client secret")
                    }
                },
                { error ->
                    Log.e("API_ERROR", error.toString())
                    onError(error.message ?: "An error occurred while fetching payment intent")
                }
            )

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }

        // You can add other API methods below as needed, following the same pattern.
    }
}
