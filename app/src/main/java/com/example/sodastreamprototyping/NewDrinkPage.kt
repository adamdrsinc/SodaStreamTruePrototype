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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NewDrinkPage(navController: NavController) {
    val context = LocalContext.current
    val drinkFlavors = context.resources.getStringArray(R.array.drink_flavors)
    val drinkBases = context.resources.getStringArray(R.array.drink_bases)

    // Initialize the custom drink with reactive ingredients
    val newDrink = remember { Drink(name = "Custom", isCustom = true, ingredients = ArrayList()) }
    val ingredientsState = remember { mutableStateOf(newDrink.ingredients) }

    MainLayout(navController = navController) { innerPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            item {
                AccordionSection(title = "Bases", items = drinkBases, newDrink = newDrink, ingredientsState = ingredientsState)
            }
            item {
                AccordionSection(title = "Flavors", items = drinkFlavors, newDrink = newDrink, ingredientsState = ingredientsState)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                CurrentDrinkSummary(newDrink = newDrink, ingredientsState = ingredientsState)
            }
        }
    }
}

@Composable
fun AccordionSection(title: String, items: Array<String>, newDrink: Drink, ingredientsState: MutableState<ArrayList<Pair<String, Int>>>) {
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
fun IngredientRow(newDrink: Drink, ingredient: String, ingredientsState: MutableState<ArrayList<Pair<String, Int>>>) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Add or increment ingredient
                val updatedIngredients = ArrayList(ingredientsState.value)
                val existingIngredient = updatedIngredients.find { it.first == ingredient }
                if (existingIngredient != null) {
                    val index = updatedIngredients.indexOf(existingIngredient)
                    updatedIngredients[index] = existingIngredient.copy(second = existingIngredient.second + 1)
                } else {
                    updatedIngredients.add(Pair(ingredient, 1))
                }
                ingredientsState.value = updatedIngredients
                Toast.makeText(context, "$ingredient added to drink.", Toast.LENGTH_SHORT).show()
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
fun CurrentDrinkSummary(newDrink: Drink, ingredientsState: MutableState<ArrayList<Pair<String, Int>>>) {
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

        if (ingredientsState.value.isEmpty()) {
            Text(
                text = "No ingredients added yet.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        } else {
            ingredientsState.value.forEachIndexed { index, (ingredient, quantity) ->
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
                            val updatedIngredients = ArrayList(ingredientsState.value)
                            if (quantity > 1) {
                                updatedIngredients[index] = ingredient to quantity - 1
                            } else {
                                updatedIngredients.removeAt(index)
                            }
                            ingredientsState.value = updatedIngredients
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
                            val updatedIngredients = ArrayList(ingredientsState.value)
                            updatedIngredients[index] = ingredient to quantity + 1
                            ingredientsState.value = updatedIngredients
                        }
                    ) {
                        Text(text = "+", fontSize = 18.sp, color = Color.Green)
                    }
                }
            }
        }
    }
}
