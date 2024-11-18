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

    var ingList1 : MutableList<Pair<Int, Int>> = mutableListOf(
        Pair(2, 1),
        Pair(3, 1)
    )
    var ingList2 : MutableList<Pair<Int, Int>> = mutableListOf(
        Pair(2, 1),
        Pair(3, 1)
    )
    var ingList3 : MutableList<Pair<Int, Int>> = mutableListOf(
        Pair(4, 1),
        Pair(5, 1)
    )

    //toMutableList() must be called to get a deep copy of the list
    var drink1 = Drink(ingList1, name = "Strawberry Creamer", quantity = 1, isCustom = false, baseDrink = 0)
    var drink2 = Drink(ingList2, name = "Something Something",quantity = 1, isCustom = false, iceQuantity = 5,
        baseDrink = 1)
    var drink3 = Drink(ingList3, name = "Other Thing", quantity = 1, isCustom = false, iceQuantity = 3, baseDrink
    = 2)

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
                Basket.addDrink(drink.copy())
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

                Text(text = "$%.2f".format(drink.getPrice()), fontSize = 16.sp, fontWeight = FontWeight.Medium)

                val drinkIngredients = drink.ingredients
                var drinkString = ""
                for(i in 0 until drinkIngredients.size){
                    val ingredientToString = context.resources.getStringArray(R.array.drink_flavors)[drinkIngredients[i].first]
                    val addedString = if(i != drinkIngredients.size - 1) ", " else ""
                    drinkString += ingredientToString + " x${drinkIngredients[i].second}" + addedString
                }

                Text(text = drinkString, fontSize = 14.sp)
                Text(text = "Ice: ${drink.iceQuantity}", fontSize = 14.sp)


                if(drink.description != null){
                    Text(text = drink.description!!, fontSize = 14.sp)
                }
            }
        }
    }
}
