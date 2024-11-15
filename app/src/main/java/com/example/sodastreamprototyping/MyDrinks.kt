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
fun MyDrinksPage(navController: NavController, onCreateDrink: (Drink) -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))

    val context = LocalContext.current
    val drinkBases = context.resources.getStringArray(R.array.drink_bases)
    val drinkIngredients = context.resources.getStringArray(R.array.drink_flavors)

    var ingList1 : SnapshotStateList<Pair<Int, Int>> = mutableStateListOf(
        Pair(0, 1),
        Pair(1, 1)
    )
    var ingList2 : SnapshotStateList<Pair<Int, Int>> = mutableStateListOf(
        Pair(2, 1),
        Pair(3, 1)
    )
    var ingList3 : SnapshotStateList<Pair<Int, Int>> = mutableStateListOf(
        Pair(4, 1),
        Pair(5, 1)
    )

    var drink1 = Drink(ingList1, name = "Custom Drink 1", quantity = 1, isCustom = true, baseDrink = 0)
    var drink2 = Drink(ingList2, name = "Custom Drink 2", quantity = 1, isCustom = true, iceQuantity = 5, baseDrink = 1)
    var drink3 = Drink(ingList3, name = "Custom Drink 3", quantity = 1, isCustom = true, iceQuantity = 3, baseDrink = 2)

    val drinks = listOf(
        drink1,
        drink2,
        drink3
    )


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CreateDrinkButton(
            navController = navController,
            editDrinkNavigaton = onCreateDrink

        )
        Spacer(modifier = Modifier.height(16.dp))

        drinks.forEach { drink ->
            DrinkCard(drink)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

@Composable
fun CreateDrinkButton(navController: NavController, editDrinkNavigaton: (Drink) -> Unit){
    Button(
        onClick = {editDrinkNavigaton(Drink(name = "new Drink", baseDrink = 0))}
    ){
        Text(
            text = "Create Drink"
        )
    }
}
