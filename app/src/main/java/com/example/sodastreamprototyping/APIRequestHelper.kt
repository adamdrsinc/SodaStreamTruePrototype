package com.example.practice

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sodastreamprototyping.Repository
import org.json.JSONObject


class ApiRequestHelper {

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080"

        /*
        * Obtains all data for singletons etc. used in the app.
        * */
        fun retrieveAllNeededData(context: Context){
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
            onError: (String) -> Unit)
        {
            val url = "$BASE_URL/auth/authenticate"

            // Create JSON object for request parameters
            val jsonParams = JSONObject().apply {
                put("username", username)
                put("password", password)
            }

            // Create a request using Volley
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, jsonParams,
                Response.Listener { response ->
                    onSuccess(response)
                },
                Response.ErrorListener { error ->
                    Log.e("API_ERROR", error.toString())
                    onError(error.message ?: "An error occurred")
                }
            )

            // Add the request to the RequestQueue
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
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            val url = "$BASE_URL/auth/register"

            // Create JSON object for request parameters
            val jsonParams = JSONObject().apply {
                put("firstname", firstname)
                put("lastname", lastname)
                put("username", username)
                put("email", email)
                put("password", password)
            }

            // Create a request using Volley
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams,
                Response.Listener { response ->
                    onSuccess()
                },
                Response.ErrorListener { error ->
                    if (error.networkResponse != null) {
                        val responseData = String(error.networkResponse.data, Charsets.UTF_8)
                        Log.e("API_ERROR", "Response Code: ${error.networkResponse.statusCode}, Error data: $responseData")
                    }
                    Log.e("API_ERROR", "Volley Error: $error")
                    onError(error.message ?: "An error occurred")
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }



            // Add the request to the RequestQueue
            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }

        // PAYMENT HANDLING
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
                    onError(error.message ?: "An error occurred")
                }
            )

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }


        // DRINK REQUEST HANDLING
        // Function to fetch ingredients data
        fun fetchIngredients(
            context: Context,
            onSuccess: (List<String>) -> Unit,
            onError: (String) -> Unit
        ) {
            val url = "${BASE_URL}/inventory/all"

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
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
                Response.ErrorListener { error ->
                    Log.e("API_ERROR", error.toString())
                    onError(error.message ?: "An error occurred")
                }
            )

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }
    }
}
