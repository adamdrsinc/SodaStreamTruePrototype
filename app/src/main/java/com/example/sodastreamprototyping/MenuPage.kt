package com.example.sodastreamprototyping

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuPage() {

    Spacer(modifier = Modifier.height(16.dp))

    val context = LocalContext.current
    val drinkBases = context.resources.getStringArray(R.array.drink_bases)
    val drinkIngredients = context.resources.getStringArray(R.array.drink_flavors)

    var ingList1 : SnapshotStateList<Pair<String, Int>> = mutableStateListOf(
        Pair(drinkIngredients[0], 1),
        Pair(drinkIngredients[1], 1)
    )
    var ingList2 : SnapshotStateList<Pair<String, Int>> = mutableStateListOf(
        Pair(drinkIngredients[2], 1),
        Pair(drinkIngredients[3], 1)
    )
    var ingList3 : SnapshotStateList<Pair<String, Int>> = mutableStateListOf(
        Pair(drinkIngredients[4], 1),
        Pair(drinkIngredients[5], 1)
    )

    var drink1 = Drink(ingList1, name = "Strawberry Creamer", price = 2.99, quantity = 1, isCustom = false, baseDrink = drinkBases[0])
    var drink2 = Drink(ingList2, name = "Something Something", price = 2.99, quantity = 1, isCustom = false, iceQuantity = 5, baseDrink = drinkBases[1])
    var drink3 = Drink(ingList3, name = "Other Thing", price = 2.99, quantity = 1, isCustom = false, iceQuantity = 3, baseDrink = drinkBases[2])

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
