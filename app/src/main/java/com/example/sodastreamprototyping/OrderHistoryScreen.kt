package com.example.sodastreamprototyping

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practice.ApiRequestHelper

data class Order(
    val id: Int,
    val description: String,
    val status: String // "open" or "closed"
)

@Composable
fun OrderHistoryScreen(orders: List<Order>) {
    val context = LocalContext.current

    val openOrders = remember { orders.filter { it.status == "open" } }
    val closedOrders = remember { orders.filter { it.status == "closed" } }

    val orderHistory = mutableListOf<MutableList<String>>()

    ApiRequestHelper.fetchOrderHistory(
        context,
        onSuccess = {
            orders ->
            for (order in orders) {
                //orderHistory.add(mutableListOf(order.description, order.status))
            }
        },
        onError = {

        })

    Column(modifier = Modifier.fillMaxSize()) {
        OpenOrdersSection(openOrders)
        ClosedOrdersSection(closedOrders)
    }
}

@Composable
fun OpenOrdersSection(orders: List<Order>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Open Orders", style = MaterialTheme.typography.titleLarge)
        orders.forEach { order ->
            OrderCard(order, buttonText = "I'm Here")
        }
    }
}

@Composable
fun ClosedOrdersSection(orders: List<Order>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Closed Orders", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(orders) { order ->
                OrderCard(order)
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, buttonText: String? = null) {
    var isClicked by remember { mutableStateOf(false) }

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
            Text(order.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            if (buttonText != null) {
                Button(
                    onClick = {
                        isClicked = true
                              },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isClicked) Color.Green else MaterialTheme.colorScheme.primary,
                        contentColor = if (isClicked) Color.White else MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(if (isClicked) "✔" else buttonText)
                }
            }
        }
    }
}


/*
@Composable
fun OrderCard(order: Order, buttonText: String? = null, onClick: () -> Unit = {}) {
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
            Text(order.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            if (buttonText != null) {
                Button(
                    onClick = onClick,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}
*/

// Sample data for testing
@Composable
fun OrderHistoryScreenDemo(navController: NavController) {
    val sampleOrders = listOf(
        Order(id = 1, description = "Order #1", status = "open"),
        Order(id = 2, description = "Order #2", status = "closed"),
        Order(id = 3, description = "Order #3", status = "closed")
    )
    OrderHistoryScreen(orders = sampleOrders)
}
