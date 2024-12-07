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

    Column {
        DemoCode.drinksDemoList.forEach { drink ->
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
