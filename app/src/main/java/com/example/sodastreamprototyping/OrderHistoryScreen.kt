package com.example.sodastreamprototyping

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.practice.ApiRequestHelper

data class Order(
    val id: Int,
    val description: String,
    val status: String // "open" or "closed"
)

@Composable
fun OrderHistoryScreen(navController: NavController) {
    // var openOrders = remember { DemoCode.sampleOrders.filter { it.status == "open" } }
    // var closedOrders = remember { DemoCode.sampleOrders.filter { it.status == "closed" } }

    var openOrders by remember { mutableStateOf<List<Order>>(emptyList()) } // use democode if payment is still broken
    var closedOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    // Fetch order history when the screen is first loaded
    LaunchedEffect(Unit) {
        ApiRequestHelper.getOrderHistory(
            context,
            onSuccess = { orders ->
                // Separate orders into open and closed
                openOrders = orders.filter { it.status == "open" }
                closedOrders = orders.filter { it.status == "closed" }
                isLoading = false
            },
            onError = { errorMessage ->
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        )
    }

    MainLayout(navController = navController){
        innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OpenOrdersSection(
                orders = openOrders,
                onOrderPickup = { order ->
                    // Generate a random locker number
                    val lockerNumber = (1..42).random()

                    // Use the orderPickup method from ApiRequestHelper
                    ApiRequestHelper.Companion.orderPickup(
                        context,
                        order.id,
                        onSuccess = {
                            // Show locker number
                            Toast.makeText(
                                context,
                                "Order picked up! Please collect from Locker #$lockerNumber",
                                Toast.LENGTH_LONG
                            ).show()

                            // Update local state to move order from open to closed
                            openOrders = openOrders.filter { it.id != order.id }
                            closedOrders = closedOrders + order.copy(status = "closed")
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show() // remove error message if payments aren't fixed
                        }
                    )
                }
            )
            ClosedOrdersSection(closedOrders)
        }
    }

}

@Composable
fun OpenOrdersSection(orders: List<Order>, onOrderPickup: (Order) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Open Orders", style = MaterialTheme.typography.titleLarge)
        orders.forEach { order ->
            AccordionOrderHistory(
                title = order.description,
                items = DemoCode.drinksDemoList, // Replace with actual list of drinks if available
                imHereButton = true,
                onImHereClick = { onOrderPickup(order) }
            )
        }
    }
}

@Composable
fun ClosedOrdersSection(orders: List<Order>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Closed Orders", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(orders) { order ->
                AccordionOrderHistory(
                    title = order.description,
                    items = DemoCode.drinksDemoList, // Replace with actual list of drinks if available
                    imHereButton = false
                )
            }
        }
    }
}

@Composable
fun AccordionOrderHistory(
    title: String,
    items: List<Drink>,
    imHereButton: Boolean = true,
    onImHereClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var isClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(all = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                if (imHereButton) {
                    Button(
                        onClick = onImHereClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text("I'm Here")
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                if (items.isNotEmpty()) {
                    items.forEachIndexed { index, item ->
                        DrinkCard(item)
                    }
                } else {
                    Text(
                        text = "Ingredients could not be retrieved. Refresh the app.",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
