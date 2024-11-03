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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDrinkPage(navController: NavController) {
    val context = LocalContext.current
    val drinkFlavors = context.resources.getStringArray(R.array.drink_flavors)
    val drinkBases = context.resources.getStringArray(R.array.drink_bases)

    // Initialize the custom drink with reactive ingredients
    val newDrink = remember { Drink(name = "Custom", isCustom = true, ingredients = mutableStateListOf<Pair<String, Int>>()) }
    val ingredientsState = newDrink.ingredients
    var drinkName by remember { mutableStateOf(newDrink.name) }
    var selectedBase by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    MainLayout(navController = navController) { innerPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            item {
                // Dropdown for base drink selection
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedBase,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        drinkBases.forEach { base ->
                            DropdownMenuItem(
                                text = { Text(base) },
                                onClick = {
                                    selectedBase = base
                                    newDrink.baseDrink = base
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                AccordionSectionIngredientRow(title = "Flavors", items = drinkFlavors, newDrink = newDrink, ingredientsState = ingredientsState)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                CurrentDrinkSummary(newDrink = newDrink, ingredientsState = ingredientsState)
            }
            item {
                TextField(
                    value = drinkName,
                    onValueChange = {
                        drinkName = it
                        newDrink.name = it
                    },
                    label = {
                        Text("Drink Name")
                    },
                    maxLines = 1,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AddToBasketButton(newDrink = newDrink)
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
fun AccordionSectionBaseRow(title: String, items: Array<String>, newDrink: Drink, ingredientsState: SnapshotStateList<Pair<String, Int>>) {
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
                    BaseRow(newDrink = newDrink, base = item, ingredientsState = ingredientsState)
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
fun BaseRow(newDrink: Drink, base: String, ingredientsState: SnapshotStateList<Pair<String,Int>>){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                newDrink.baseDrink = base
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = base,
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

@Composable
fun AddToBasketButton(newDrink: Drink) {
    val context = LocalContext.current
    Button(
        onClick = {
            Basket.addDrink(newDrink)
            Toast.makeText(context, "Drink added to basket.", Toast.LENGTH_SHORT).show()
        }
    ) {
        Text(text = "Add to Basket")
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
            /*if (existingIngredient != null) {
                val index = ingredientsState.indexOf(existingIngredient)
                ingredientsState[index] = existingIngredient.copy(second = existingIngredient.second + 1)
            } else {
                ingredientsState.add(Pair(ingredient, 1))
            }*/
            return true
        } else {
            return false
        }
    } else {
        if (newDrink.addIngredient(ingredient)) {
            //ingredientsState.add(Pair(ingredient, 1))
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
            /*if (existingIngredient != null) {
                val index = ingredientsState.indexOf(existingIngredient)
                val newQuantity = existingIngredient.second - 1
                if (newQuantity > 0) {
                    ingredientsState[index] = existingIngredient.copy(second = newQuantity)
                } else {
                    ingredientsState.removeAt(index)
                }
            }*/
            return true
        } else {
            return false
        }
    } else {
        return false
    }
}