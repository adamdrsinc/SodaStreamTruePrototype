package com.example.sodastreamprototyping

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.practice.ApiRequestHelper

@Composable
fun MyDrinksPage(navController: NavController, onCreateDrink: (Drink) -> Unit) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val drinkBases = context.resources.getStringArray(R.array.drink_bases)
    val drinkIngredients = context.resources.getStringArray(R.array.drink_flavors)

    // TODO: these lines are test data, delete them when correct implementation is done
    /*
    var ingList1 : List<Pair<Int, Int>> = listOf(
        Pair(0, 1),
        Pair(1, 1)
    )
    var ingList2 : List<Pair<Int, Int>> = listOf(
        Pair(2, 1),
        Pair(3, 1)
    )
    var ingList3 : List<Pair<Int, Int>> = listOf(
        Pair(4, 1),
        Pair(5, 1)
    )

    var testDrink1 = Drink(ingList1.toMutableList(), name = "Custom Drink 1", quantity = 1, isCustom = true, baseDrink = 0)
    var testDrink2 = Drink(ingList2.toMutableList(),
        name = "Custom Drink 2", quantity = 1, isCustom = true, iceQuantity = 5, baseDrink = 1)
    var testDrink3 = Drink(ingList3.toMutableList(),
        name = "Custom Drink 3", quantity = 1, isCustom = true, iceQuantity = 3, baseDrink = 2)

    val testDrinks = listOf(
        testDrink1,
        testDrink2,
        testDrink3
    )

    val drinks = testDrinks
    */

    // TODO: get saved drinks from server and store them in drinks

    var drinks = remember { mutableStateListOf<Drink>() }

    LaunchedEffect(Unit) {
        ApiRequestHelper.fetchSavedDrinks(
            context = context,
            onSuccess = { savedDrinks ->
                drinks = savedDrinks as SnapshotStateList<Drink>
            },
            onError = { error ->
                // Handle error, maybe show a toast or log it
                Toast.makeText(context, "Error fetching saved drinks: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(bottom = 56.dp) // Add padding to avoid overlap with the button
        ) {
            drinks.forEach { drink ->
                DrinkCard(drink)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        CreateDrinkButton(
            navController = navController,
            editDrinkNavigaton = onCreateDrink,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
fun CreateDrinkButton(navController: NavController, editDrinkNavigaton: (Drink) -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = {editDrinkNavigaton(Drink(name = "new Drink", baseDrink = 0))} ,
        modifier = modifier
    ){
        Text(
            text = "Create Drink"
        )
    }
}