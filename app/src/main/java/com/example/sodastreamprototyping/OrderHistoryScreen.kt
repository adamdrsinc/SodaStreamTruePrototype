package com.example.sodastreamprototyping

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sodastreamprototyping.viewModel.OrdersViewModel

data class Order(
    val id: Int,
    val description: String,
    val status: String // "open" or "closed"
)

@Composable
fun OrderHistoryScreen(navController: NavController, onNotified: () -> Unit) {
    val viewModel : OrdersViewModel = hiltViewModel()
    val orders by viewModel.orders.collectAsState()
    val orderNotified = viewModel.orderNotified

    LaunchedEffect(orderNotified.value) {
        if(orderNotified.value){
            onNotified()
            viewModel.orderNotified.value = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
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

        Text("Open Orders", style = MaterialTheme.typography.titleLarge)
        orders?.forEach { order ->
            if(!order.second) {
                OrderCard(order.first, buttonText = "I'm Here", onClick = {
                    viewModel.amHere(order.first)
                })
            }
        }
    }
}

@Composable
fun OrderCard(id: Int, buttonText: String? = null, onClick: () -> Unit = {}) {
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
            Text("Order # $id", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            if (buttonText != null) {
                Button(onClick = onClick) {
                    Text(buttonText)
                }
            }
        }
    }
}

// Sample data for testing
@Composable
fun OrderHistoryScreenDemo(navController: NavController) {
    val context = LocalContext.current
    OrderHistoryScreen(navController){Toast.makeText(context, "Order available for pickup", Toast.LENGTH_LONG).show()}
}
