package com.example.sodastreamprototyping

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDrinkPage(navController: NavController) {
    val context = LocalContext.current

    val drink = remember { Drink(name = "Custom", isCustom = true, ingredients = mutableStateListOf<Pair<String, Int>>()) }
    val drinkFlavors = context.resources.getStringArray(R.array.drink_flavors)

    val ingredientsState = remember { drink.ingredients }
    var drinkName by remember { mutableStateOf(drink.name) }

    MainLayout(navController = navController) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "New Drink",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DropdownMenuDrinkBases(drink)

            Spacer(modifier = Modifier.height(16.dp))

            // Accordion for ingredients
            AccordionSectionIngredientRow(
                title = "Ingredients",
                items = drinkFlavors,
                newDrink = drink,
                ingredientsState = ingredientsState
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TextField to change the drink name
            TextField(
                value = drinkName,
                onValueChange = {
                    drinkName = it
                    drink.name = it
                },
                label = { Text("Drink Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display current selections
            CurrentDrinkSummary(newDrink = drink, ingredientsState = ingredientsState)

            Spacer(modifier = Modifier.height(16.dp))

            IceQuantitySlider(drink)

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = {
                    // Save changes to the drink
                    Toast.makeText(context, "Drink updated.", Toast.LENGTH_SHORT).show()
                    Basket.addDrink(drink)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}

@Composable
fun AccordionSectionIngredientRow(title: String, items: Array<String>, newDrink: Drink, ingredientsState: SnapshotStateList<Pair<String, Int>>) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp)
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                items.forEach { item ->
                    IngredientRow(newDrink = newDrink, ingredient = item, ingredientsState = ingredientsState)
                }
            }
        }
    }
}

@Composable
fun IngredientRow(newDrink: Drink, ingredient: String, ingredientsState: SnapshotStateList<Pair<String, Int>>) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val ingredientIncreased = incrementIngredient(
                    newDrink = newDrink,
                    ingredient = ingredient,
                    ingredientsState = ingredientsState
                )
                if (!ingredientIncreased) {
                    Toast.makeText(context, "Syrup count cannot exceed ${Drink.MAX_PUMP_COUNT}", Toast.LENGTH_SHORT).show()
                }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredient,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CurrentDrinkSummary(newDrink: Drink, ingredientsState: SnapshotStateList<Pair<String, Int>>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Current Drink: ${newDrink.name}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (ingredientsState.isEmpty()) {
            Text(
                text = "No ingredients added yet.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        } else {
            ingredientsState.forEachIndexed { index, (ingredient, quantity) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ingredient,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    // Decrement Button
                    IconButton(
                        onClick = {
                            val decreasedIngredient = decrementIngredient(
                                newDrink = newDrink,
                                ingredient = ingredient,
                                ingredientsState = ingredientsState
                            )
                            if (!decreasedIngredient) {
                                Toast.makeText(context, "Syrup count cannot ${Drink.MAX_PUMP_COUNT}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text(text = "-", fontSize = 18.sp, color = Color.Red)
                    }

                    // Quantity Display
                    Text(
                        text = quantity.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Increment Button
                    IconButton(
                        onClick = {
                            val increasedIngredient = incrementIngredient(
                                newDrink = newDrink,
                                ingredient = ingredient,
                                ingredientsState = ingredientsState
                            )
                            if (!increasedIngredient) {
                                Toast.makeText(context, "Syrup count cannot exceed ${Drink.MAX_PUMP_COUNT}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text(text = "+", fontSize = 18.sp, color = Color.Green)
                    }
                }
            }
        }
    }
}

fun incrementIngredient(
    newDrink: Drink,
    ingredient: String,
    ingredientsState: SnapshotStateList<Pair<String, Int>>
): Boolean {
    val existingIngredient = ingredientsState.find { it.first == ingredient }

    if (newDrink.hasIngredient(ingredient) != null) {
        if (newDrink.incrementIngredient(ingredient)) {
            return true
        } else {
            return false
        }
    } else {
        if (newDrink.addIngredient(ingredient)) {
            return true
        } else {
            return false
        }
    }
}

fun decrementIngredient(
    newDrink: Drink,
    ingredient: String,
    ingredientsState: SnapshotStateList<Pair<String, Int>>
): Boolean {
    val existingIngredient = ingredientsState.find { it.first == ingredient }

    if (newDrink.hasIngredient(ingredient) != null) {
        if (newDrink.decrementIngredient(ingredient)) {
            return true
        } else {
            return false
        }
    } else {
        return false
    }
}