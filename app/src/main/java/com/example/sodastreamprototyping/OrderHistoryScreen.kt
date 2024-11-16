package com.example.sodastreamprototyping

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Order(
    val id: Int,
    val description: String,
    val status: String // "open" or "closed"
)

@Composable
fun OrderHistoryScreen(
    navController: NavController,
    orders: List<Order>,
    hasOpenOrders: MutableState<Boolean> // Pass openOrderCount as a state variable
) {
    val openOrders = remember { orders.filter { it.status == "open" } }
    val closedOrders = remember { orders.filter { it.status == "closed" } }

    hasOpenOrders.value = orders.any { it.status == "open" }

    Column(modifier = Modifier.fillMaxSize()) {
        // Back button at the top left
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(onClick = { navController.navigate("home") }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Home"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OpenOrdersSection(openOrders)
        ClosedOrdersSection(closedOrders)
    }
}

@Composable
fun OpenOrdersSection(orders: List<Order>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Open Orders", style = MaterialTheme.typography.titleLarge)
        orders.forEach { order ->
            OrderCard(order, buttonText = "I'm Here", onClick = {
                // Placeholder for action, like updating order status
            })
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
            if (buttonText != null) {
                Button(onClick = onClick) {
                    Text(buttonText)
                }
            }
        }
    }
}

// Sample usage with dynamic open order count
@Composable
fun MainWithOrderHistory(navController: NavController, orders: List<Order>) {
    val hasOpenOrders = remember { mutableStateOf(false) }

    MainLayout(navController = navController, hasOpenOrders = hasOpenOrders.value) {
        OrderHistoryScreen(navController, orders, hasOpenOrders)
    }
}

@Composable
fun OrderHistoryScreenDemo(navController: NavController) {
    // Define sample orders directly within the demo
    val sampleOrders = listOf(
        Order(id = 1, description = "Order #1", status = "open"),
        Order(id = 2, description = "Order #2", status = "closed"),
        Order(id = 3, description = "Order #3", status = "open")
    )

    // Mutable state to keep track of open orders count
    val hasOpenOrders = remember { mutableStateOf(false) }

    // Pass sampleOrders to the OrderHistoryScreen for testing
    OrderHistoryScreen(navController = navController, orders = sampleOrders, hasOpenOrders = hasOpenOrders)
}
