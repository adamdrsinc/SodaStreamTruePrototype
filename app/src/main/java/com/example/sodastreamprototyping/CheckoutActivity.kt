package com.example.sodastreamprototyping

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sodastreamprototyping.ui.theme.SodaStreamPrototypingTheme
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.google.android.gms.wallet.PaymentDataRequest
import com.example.practice.ApiRequestHelper
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONArray
import org.json.JSONObject

class CheckoutActivity : ComponentActivity() {
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var paymentsClient: PaymentsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve basket data from intent extras
        val basketItems = intent.getStringExtra("BASKET_ITEMS") ?: ""
        val cartTotal = intent.getDoubleExtra("CART_TOTAL", 0.0)

        // Initialize Stripe Payment Configuration (Replace with your publishable key)
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51PIcgvP2OT2CbOwHRleQOGlCEIDacOv5XatQoaBo3M0ooqmgkJxJ38WIf1tySly8KRCTQlQv6Xw2HTalf7kMuUTT00WGUvxoAK"
        )

        // Initialize PaymentSheet and PaymentsClient
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        paymentsClient = Wallet.getPaymentsClient(
            this,
            Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
        )

        setContent {
            SodaStreamPrototypingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CheckoutScreen(
                        basketItems = basketItems,
                        cartTotal = cartTotal,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    fun CheckoutScreen(basketItems: String, cartTotal: Double, modifier: Modifier = Modifier) {
        var paymentIntentClientSecret by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        val context = LocalContext.current

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Checkout title
            Text(text = "Checkout", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Display cart details and total
            Text(text = "Items: $basketItems")
            Text(text = "Total: $$cartTotal")
            Spacer(modifier = Modifier.height(16.dp))

            // Divider with "or" text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Divider(modifier = Modifier.weight(1f))
                Text("or", modifier = Modifier.padding(horizontal = 8.dp))
                Divider(modifier = Modifier.weight(1f))
            }

            // Google Pay Button
            Button(
                onClick = {
                    isLoading = true
                    val googlePayRequest = createGooglePayRequest(cartTotal)
                    val task = paymentsClient.loadPaymentData(googlePayRequest)
                    task.addOnCompleteListener { result ->
                        isLoading = false
                        if (result.isSuccessful) {
                            val paymentData = result.result
                            // Handle successful payment data
                            Toast.makeText(context, "Google Pay Successful", Toast.LENGTH_LONG).show()
                        } else {
                            // Handle error
                            Toast.makeText(context, "Google Pay Failed", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Pay with Google Pay")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Payment Button to use PaymentSheet
            Button(
                onClick = {
                    isLoading = true
                    ApiRequestHelper.fetchPaymentIntent(
                        context = context,
                        amount = cartTotal,
                        currency = "usd",
                        onSuccess = { clientSecret ->
                            paymentIntentClientSecret = clientSecret
                            presentPaymentSheet(paymentIntentClientSecret)
                            isLoading = false
                        },
                        onError = { errorMessage ->
                            isLoading = false
                            // Show error message to the user
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Make Payment with Card")
            }
        }
    }

    private fun presentPaymentSheet(paymentIntentClientSecret: String) {
        val configuration = PaymentSheet.Configuration(
            merchantDisplayName = "Example, Inc.",
            allowsDelayedPaymentMethods = true
        )
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration)
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                // Payment was successful, show confirmation message
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show()
                // You may want to navigate to a success screen or update your UI accordingly
            }
            is PaymentSheetResult.Canceled -> {
                // User canceled the payment, show a snackbar or alert
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_LONG).show()
            }
            is PaymentSheetResult.Failed -> {
                // Handle the error, display the message to the user
                Toast.makeText(this, "Payment Failed: ${paymentSheetResult.error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createGooglePayRequest(totalPrice: Double): PaymentDataRequest {
        val paymentDataRequestJson = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods", JSONArray().apply {
                put(createAllowedPaymentMethods())
            })
            put("transactionInfo", createTransactionInfo(totalPrice))
            put("merchantInfo", createMerchantInfo())
        }
        return PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
    }

    private fun createAllowedPaymentMethods(): JSONObject {
        return JSONObject().apply {
            put("type", "CARD")
            put("parameters", JSONObject().apply {
                put("allowedAuthMethods", JSONArray().apply {
                    put("PAN_ONLY")
                    put("CRYPTOGRAM_3DS")
                })
                put("allowedCardNetworks", JSONArray().apply {
                    put("AMEX")
                    put("DISCOVER")
                    put("JCB")
                    put("MASTERCARD")
                    put("VISA")
                })
                put("billingAddressRequired", true)
                put("billingAddressParameters", JSONObject().apply {
                    put("format", "FULL")
                    put("phoneNumberRequired", false)
                })
            })
            put("tokenizationSpecification", JSONObject().apply {
                put("type", "PAYMENT_GATEWAY")
                put("parameters", JSONObject().apply {
                    put("gateway", "stripe")
                    put("stripe:publishableKey", "pk_test_51PIcgvP2OT2CbOwHRleQOGlCEIDacOv5XatQoaBo3M0ooqmgkJxJ38WIf1tySly8KRCTQlQv6Xw2HTalf7kMuUTT00WGUvxoAK")
                    put("stripe:version", "2020-08-27")
                })
            })
        }
    }

    private fun createTransactionInfo(totalPrice: Double): JSONObject {
        return JSONObject().apply {
            put("totalPrice", String.format("%.2f", totalPrice))
            put("totalPriceStatus", "FINAL")
            put("currencyCode", "USD")
        }
    }

    private fun createMerchantInfo(): JSONObject {
        return JSONObject().apply {
            put("merchantName", "Example, Inc.")
            // Additional merchant information can be added here
        }
    }
}
