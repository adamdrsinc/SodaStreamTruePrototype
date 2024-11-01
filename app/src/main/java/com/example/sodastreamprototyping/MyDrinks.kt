package com.example.sodastreamprototyping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MyDrinksPage(navController: NavController) {
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

    var drink1 = Drink(ingList1, name = "Custom Drink 1", price = 2.99, quantity = 1, isCustom = false)
    var drink2 = Drink(ingList2, name = "Custom Drink 2", price = 2.99, quantity = 1, isCustom = false, iceQuantity = 5)
    var drink3 = Drink(ingList3, name = "Custom Drink 3", price = 2.99, quantity = 1, isCustom = false, iceQuantity = 3)

    val drinks = listOf(
        drink1,
        drink2,
        drink3
    )


    Column {
        CreateDrinkButton(navController = navController)

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
