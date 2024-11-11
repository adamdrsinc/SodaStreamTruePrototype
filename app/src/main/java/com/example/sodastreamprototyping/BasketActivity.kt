package com.example.sodastreamprototyping

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.practice.ApiRequestHelper
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetContract

@Composable
fun ShoppingBasket(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val config = LocalConfiguration.current

    // Initialize Stripe Payment Configuration
    LaunchedEffect(Unit) {
        PaymentConfiguration.init(
            context,
            "pk_test_51PIcgvP2OT2CbOwHRleQOGlCEIDacOv5XatQoaBo3M0ooqmgkJxJ38WIf1tySly8KRCTQlQv6Xw2HTalf7kMuUTT00WGUvxoAK"
        )
    }

    var isLoading by remember { mutableStateOf(false) }
    var paymentIntentClientSecret by remember { mutableStateOf("") }

    // PaymentSheet launcher
    val paymentSheetLauncher = rememberLauncherForActivityResult(
        contract = PaymentSheetContract(),
        onResult = { paymentSheetResult ->
            onPaymentSheetResult(paymentSheetResult, context)
        }
    )

    // Calculate the total amount from basket items
    val totalAmount = Basket.basketDrinks.sumOf { it.price * it.quantity } // Assuming `price` is defined

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back button at the top left
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(onClick = { navController.navigate("home") }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Basket items display
        BasketItems(navController)

        // Checkout Button (trigger payment directly)
        CheckoutButton(
            isLoading = isLoading,
            onCheckoutClick = {
                isLoading = true
                ApiRequestHelper.fetchPaymentIntent(
                    context = context,
                    amount = totalAmount,
                    currency = "usd",
                    onSuccess = { clientSecret ->
                        paymentIntentClientSecret = clientSecret
                        isLoading = false
                        presentPaymentSheet(paymentSheetLauncher, clientSecret)
                    },
                    onError = { errorMessage ->
                        isLoading = false
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            }
        )
    }
}

@Composable
fun CheckoutButton(
    isLoading: Boolean,
    onCheckoutClick: () -> Unit
) {
    Button(
        onClick = onCheckoutClick,
        enabled = !isLoading
    ) {
        Text(text = if (isLoading) "Processing..." else "Checkout")
    }
}

private fun presentPaymentSheet(
    paymentSheetLauncher: ActivityResultLauncher<PaymentSheetContract.Args>,
    paymentIntentClientSecret: String
) {
    val configuration = PaymentSheet.Configuration(
        merchantDisplayName = "Soda Sensations",
        allowsDelayedPaymentMethods = true
    )
    paymentSheetLauncher.launch(
        PaymentSheetContract.Args.createPaymentIntentArgs(
            paymentIntentClientSecret,
            configuration
        )
    )
}

private fun onPaymentSheetResult(
    paymentSheetResult: PaymentSheetResult,
    context: Context
) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Completed -> {
            Toast.makeText(context, "Payment Successful", Toast.LENGTH_LONG).show()
            Log.i("PaymentSuccess", "Payment completed successfully.")
        }
        is PaymentSheetResult.Canceled -> {
            Toast.makeText(context, "Payment Canceled", Toast.LENGTH_LONG).show()
        }
        is PaymentSheetResult.Failed -> {
            Log.e("PaymentError", "Payment failed: ${paymentSheetResult.error.localizedMessage}", paymentSheetResult.error)
            Toast.makeText(context, "Payment Failed: ${paymentSheetResult.error.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}


@Composable
fun BasketItems(navController: NavController) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val screenHeight = config.screenHeightDp

    val drinks = remember {
        mutableStateListOf<Drink>().apply { addAll(Basket.basketDrinks) }
    }

    LazyColumn(
        modifier = Modifier
            .height((screenHeight * 0.80).dp)
            .width(screenWidth.dp)
    ) {
        itemsIndexed(drinks) { index, drink ->
            BasketItemCard(
                drink = drink,
                screenWidth = screenWidth,
                onEdit = { navController.navigate(Screen.Edit.withArgs(drink.drinkID.toString())) },
                onDelete = {
                    Basket.basketDrinks.removeAt(index)
                    drinks.removeAt(index)
                }
            )
        }
    }
}


@Composable
fun BasketItemCard(
    drink: Drink,
    screenWidth: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val baseArray = LocalContext.current.resources.getStringArray(R.array.drink_bases)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
            .width((screenWidth * 0.90).dp)
            .border(2.dp, Color.Black)
            .padding(24.dp)
    ) {
        Text(
            text = drink.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(text = "Base: ${baseArray[drink.baseDrink]}", fontSize = 16.sp)

        DrinkRow("Quantity", drink.quantity.toString(), screenWidth)
        DrinkRow("Ice", drink.iceQuantity.toString(), screenWidth)

        drink.ingredients.forEach { ingredient ->
            DrinkRow(ingredient.first, ingredient.second.toString(), screenWidth)
        }

        ActionButtons(isCustom = drink.isCustom, onEdit = onEdit, onDelete = onDelete)
    }
}

@Composable
fun DrinkRow(label: String, value: String, screenWidth: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .width((screenWidth * 0.60).dp)
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.width((screenWidth * 0.40).dp),
            textAlign = TextAlign.Start
        )
        Text(
            text = value,
            modifier = Modifier.width((screenWidth * 0.40).dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ActionButtons(isCustom: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row {
        Button(onClick = onEdit, modifier = Modifier.padding(4.dp)) {
            Text(text = "Edit")
        }

        Button(onClick = onDelete, modifier = Modifier.padding(4.dp)) {
            Text(text = "Delete")
        }
    }
}

