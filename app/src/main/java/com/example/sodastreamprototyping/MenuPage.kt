package com.example.sodastreamprototyping

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuPage() {

    Spacer(modifier = Modifier.height(16.dp))

    var ingList1 = arrayListOf<Pair<String, Int>>(
        Pair("Strawberry Cream", 1),
        Pair("Coconut", 1)
    )

    var ingList2 = arrayListOf<Pair<String, Int>>(
        Pair("Creamer", 1),
        Pair("Coconut", 1)
    )

    var ingList3 = arrayListOf<Pair<String, Int>>(
        Pair("Strawberry Cream", 1),
        Pair("Blueberry", 1)
    )



    var drink1 = Drink(ingList1, name = "Strawberry Coconut", price = 2.99, quantity = 1, isCustom = false)
    var drink2 = Drink(ingList2, name = "Coco Creamer", price = 2.99, quantity = 1, isCustom = false, iceQuantity = 5)
    var drink3 = Drink(ingList3, name = "Blueberry Cream", price = 2.99, quantity = 1, isCustom = false, iceQuantity = 3)

    val drinks = listOf(
        drink1,
        drink2,
        drink3
    )

    Column {
        drinks.forEach { drink ->
            DrinkCard(drink)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


//data class Drink(val name: String, val price: String, val description: String)

@Composable
fun DrinkCard(drink: Drink) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Basket.addDrink(drink)
                Toast.makeText(context, "${drink.name} added to basket.", Toast.LENGTH_SHORT).show()
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = drink.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = drink.price.toString(), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))

                if(drink.description != null){
                    Text(text = drink.description!!, fontSize = 14.sp)
                }
            }
        }
    }
}
