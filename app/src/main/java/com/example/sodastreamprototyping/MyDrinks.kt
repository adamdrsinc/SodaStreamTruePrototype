package com.example.sodastreamprototyping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MyDrinksPage(navController: NavController) {
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

    var drink1 = Drink(ingList1, name = "Custom Drink 1", price = 2.99, quantity = 1, isCustom = true, baseDrink = drinkBases[0])
    var drink2 = Drink(ingList2, name = "Custom Drink 2", price = 2.99, quantity = 1, isCustom = true, iceQuantity = 5, baseDrink = drinkBases[1])
    var drink3 = Drink(ingList3, name = "Custom Drink 3", price = 2.99, quantity = 1, isCustom = true, iceQuantity = 3, baseDrink = drinkBases[2])

    val drinks = listOf(
        drink1,
        drink2,
        drink3
    )


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CreateDrinkButton(navController = navController)
        Spacer(modifier = Modifier.height(16.dp))

        drinks.forEach { drink ->
            DrinkCard(drink)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

@Composable
fun CreateDrinkButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate(Screen.NewDrink.route)
        }
    ){
        Text(
            text = "Create Drink"
        )
    }
}
