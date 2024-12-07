package com.example.practice

import android.content.Context
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sodastreamprototyping.Drink
import com.example.sodastreamprototyping.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.toString

@Singleton
class ApiRequestHelper @Inject constructor(@ApplicationContext val context: Context) {
    val requestQueue: RequestQueue = Volley.newRequestQueue(context)
//    val sharedPreferences = context.getSharedPreferences(UserPreferences)

    // Function to handle login request
    fun makeLoginRequest(
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
                UserPreferences.login(context, response.getString("access_token"), response.getString("refresh_token"))
                onSuccess(response)
            },
            { error ->
                Log.e("API_ERROR", error.toString())
                onError(error.message ?: "An error occurred during login")
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    // Function to handle sign-up request
    fun makeSignUpRequest(
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

        requestQueue.add(jsonObjectRequest)
    }

    fun getOrderHistory(
        onSuccess: (List<Pair<Int, Boolean>>) -> Unit,
        onError: () -> Unit
    ) {
        val url = "${BASE_URL}/orders/history"

        val jsonObjectRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val openOrders = mutableListOf<Pair<Int, Boolean>>()
                for(i in 0 until response.length()){
                    openOrders.add(Pair(
                        response.getJSONObject(i).getString("id").toInt(),
                        response.getJSONObject(i).getString("completed").toBoolean()))
                }
                //TODO parse request, get list of Pair<Int, Boolean>
                val mock = arrayOf(Pair(1, false))
                onSuccess(openOrders.toList())
            },
            { error ->
                Log.e("API_ERROR", error.toString())
                onError()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${UserPreferences.getAccessToken(context)}"
                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)
    }

    fun completeOrder(
        id: Int,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ){
        //Add the order id to the endpoint
        var urlBase = "${BASE_URL}/orders/here/"
        var orderID = id.toString()

        val url = urlBase + orderID

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, null,
            {
                    response ->
                onSuccess()
            },
            {
                    error ->
                Log.e("API_ERROR", error.toString())
                onError()
            }

        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${UserPreferences.getAccessToken(context)}"
                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)
    }


        companion object {
        private const val BASE_URL = "http://10.0.2.2:8080"

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
        fun getIngredients(
            context: Context,
            onSuccess: (Map<String, MutableList<String>>) -> Unit,
            onError: (String) -> Unit
        ) {
            val url = "$BASE_URL/inventory/available"
            val accessToken = UserPreferences.getAccessToken(context)

            val jsonObjectRequest = object : JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->
                    try {
                        Log.i("API_RESPONSE", response.toString())
                        val bases = mutableListOf<String>()
                        val ingredients = mutableListOf<String>()

                        for (i in 0 until response.length()) {
                            val jsonObject = response.getJSONObject(i)
                            val name = jsonObject.getString("name")
                            val isBase = jsonObject.getBoolean("isBase")

                            if (isBase) {
                                bases.add(name)
                            } else {
                                ingredients.add(name)
                            }
                        }

                        val syrupMap = mapOf("bases" to bases, "ingredients" to ingredients)
                        onSuccess(syrupMap)
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
                                    getIngredients(context, onSuccess, onError)
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


        fun createOrder(
            context: Context,
            drinkList: List<Drink>,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit
        ) {
            val url = "$BASE_URL/orders/create"
            val accessToken = UserPreferences.getAccessToken(context)

            val jsonParams = JSONArray().apply {
                for (drink in drinkList) {
                    val drinkJson = JSONObject().apply {
                        put("baseIngredientId", drink.baseDrink)
                        val flavorIngredientsArray = JSONArray().apply {
                            for (flavor in drink.ingredients) {
                                val flavorJson = JSONObject().apply {
                                    put("flavorIngredientId", flavor.first)
                                    put("quantity", flavor.second)
                                }
                                put(flavorJson)
                            }
                        }
                        put("flavorIngredients", flavorIngredientsArray)
                    }
                    put(drinkJson)
                }
            }

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, url, null,
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
                    onError(error.message ?: "An error occurred while creating the order")
                }
            ) {
                override fun getBody(): ByteArray {
                    return jsonParams.toString().toByteArray(Charsets.UTF_8)
                }

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $accessToken"
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)
        }

        fun getOrderHistory(
            context: Context,
            onSuccess: () -> Unit,
            onError: () -> Unit
        ){
            val url = "${BASE_URL}/orders/history"

            val jsonObjectRequest = object : JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->
                    onSuccess()
                },
                { error ->
                    Log.e("API_ERROR", error.toString())
                    onError()
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer ${UserPreferences.getAccessToken(context)}"
                    return headers
                }
            }

            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            requestQueue.add(jsonObjectRequest)

        }

        fun completeOrder(
            context: Context,
            onSuccess: () -> Unit,
            onError: () -> Unit
        ){
            //Add the order id to the endpoint
            var urlBase = "${BASE_URL}/orders/here/"
            var orderID = ""

            val url = urlBase + orderID

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, url, null,
                {
                    response ->
                    onSuccess()
                },
                {
                    error ->
                    Log.e("API_ERROR", error.toString())
                    onError()
                }

            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer ${UserPreferences.getAccessToken(context)}"
                    return headers
                }
            }
        }


        // You can add other API methods below as needed, following the same pattern.
    }
}
