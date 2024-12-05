package com.example.sodastreamprototyping

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
    val openOrders = remember { DemoCode.sampleOrders.filter { it.status == "open" } }
    val closedOrders = remember { DemoCode.sampleOrders.filter { it.status == "closed" } }

    MainLayout(navController = navController){
        innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OpenOrdersSection(openOrders)
            ClosedOrdersSection(closedOrders)
        }
    }

}

@Composable
fun OpenOrdersSection(orders: List<Order>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Open Orders", style = MaterialTheme.typography.titleLarge)
        orders.forEach { order ->
            AccordionOrderHistory(
                title = order.description,
                items = DemoCode.drinksDemoList, // Replace with actual list of drinks if available
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
    imHereButton: Boolean = true
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
                        onClick = {
                            isClicked = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isClicked) Color.Green else MaterialTheme.colorScheme.primary,
                            contentColor = if (isClicked) Color.White else MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(if (isClicked) "✔" else "I'm Here")
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
