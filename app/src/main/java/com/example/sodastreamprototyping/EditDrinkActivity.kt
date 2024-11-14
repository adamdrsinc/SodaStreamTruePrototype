package com.example.sodastreamprototyping

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.practice.ApiRequestHelper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDrinkPage(navController: NavController, drink: Drink?) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var buttonText = "Save Changes"
    var titleText: String = "Edit Drink"

    if (drink == null) {
        titleText = "New Drink"
        buttonText = "Add Drink"
    }

    var drinkCopy = drink
    if (drinkCopy == null) {
        drinkCopy = remember {
            Drink(
                name = "New Drink",
                isCustom = true,
                ingredients = mutableStateListOf<Pair<Int, Int>>()
            )
        }
    }

    // TODO: Get drink flavors from DB, not from resources
    val drinkFlavors = context.resources.getStringArray(R.array.drink_flavors)
    // TODO: Replace above line with the code below
    var drinkFlavorsFromDB: List<String> = listOf()
    ApiRequestHelper.fetchIngredients(
        context = context,
        onSuccess = { ingredients ->
            drinkFlavorsFromDB = ingredients
        },
        onError = { error ->
            Toast.makeText(context, "Error fetching ingredients: $error", Toast.LENGTH_SHORT).show()
        }
    )

    val ingredientsState = remember { drinkCopy.ingredients }
    var drinkQuantity = remember { drinkCopy.quantity }
    var drinkName by remember { mutableStateOf(drinkCopy.name) }

    // LaunchedEffect to observe changes in ingredientsState
    LaunchedEffect(ingredientsState) {
        // Trigger recomposition when ingredientsState changes
    }

    MainLayout(navController = navController) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Title
            SectionTitle(text = titleText, centered = true)
            Spacer(modifier = Modifier.height(18.dp))

            // Drink Bases Dropdown
            SectionTitle(text = "Bases")
            DropdownMenuDrinkBases(drink = drinkCopy)
            Spacer(modifier = Modifier.height(18.dp))

            // Accordion for ingredients
            SectionTitle(text = "Ingredients")
            AccordionSectionIngredientRow(
                title = "Ingredients",
                items = drinkFlavors,
                newDrink = drinkCopy,
                ingredientsState = ingredientsState
            )
            Spacer(modifier = Modifier.height(18.dp))

            // TextField to change the drink name
            SectionTitle("Drink Name")
            TextField(
                value = drinkName,
                onValueChange = {
                    drinkName = it
                    drinkCopy.name = it
                },
                label = { Text("Drink Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))

            // Display current selections
            SectionTitle("Current Drink Summary")
            CurrentDrinkSummary(newDrink = drinkCopy, ingredientsState = ingredientsState)
            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle("Ice Quantity")
            IceQuantitySlider(drinkCopy)
            Spacer(modifier = Modifier.height(18.dp))

            // Save button
            Button(
                onClick = {
                    // Save changes to the drink
                    if (Basket.getDrinks().find { it.drinkID == drink?.drinkID } == null) {
                        Basket.addDrink(drinkCopy)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun SectionTitle(text: String, color: Color = Color.LightGray,
                 centered: Boolean = false) {
    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(color)
            .padding(8.dp),
        textAlign = if (centered) TextAlign.Center else TextAlign.Start
    )
}

@Composable
fun AccordionSectionIngredientRow(
    title: String,
    items: Array<String>,
    newDrink: Drink,
    ingredientsState: SnapshotStateList<Pair<Int, Int>>
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    IngredientRow(
                        newDrink = newDrink,
                        ingredient = Pair(index, item),
                        ingredientsState = ingredientsState
                    )
                }
            }
        }
    }
}

@Composable
fun IngredientRow(newDrink: Drink, ingredient: Pair<Int, String>,
                  ingredientsState: SnapshotStateList<Pair<Int, Int>>,
                  aiRecommended: Boolean = false) {
    val context = LocalContext.current

    val modifier = Modifier
        .background(if (aiRecommended) Color.Yellow else Color.Transparent)
        .fillMaxWidth()
        .clickable {
            val ingredientIncreased = incrementIngredient(
                newDrink = newDrink,
                ingredient = ingredient.first,
                ingredientsState = ingredientsState
            )
            if (!ingredientIncreased) {
                Toast.makeText(
                    context,
                    "Syrup count cannot exceed ${Drink.MAX_PUMP_COUNT}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        .padding(16.dp)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredient.second,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CurrentDrinkSummary(newDrink: Drink, ingredientsState: SnapshotStateList<Pair<Int, Int>>) {
    val context = LocalContext.current
    val drinkFlavors = context.resources.getStringArray(R.array.drink_flavors)

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
                        text = drinkFlavors[ingredient],
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
                                Toast.makeText(
                                    context,
                                    "Syrup count cannot ${Drink.MAX_PUMP_COUNT}",
                                    Toast.LENGTH_SHORT
                                ).show()
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
fun IceQuantitySlider(drink: Drink?) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    if (drink == null) {
        return
    }

    // State to hold the slider position
    val sliderPosition = remember { mutableStateOf(drink.iceQuantity.toFloat()) } // 0f to 1f range

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Text to display the current value of the slider
        Text(text = "Ice amount: ${sliderPosition.value.toInt()}")

        // Slider
        Slider(
            value = sliderPosition.value,
            onValueChange = { newValue ->
                sliderPosition.value = newValue
                drink.iceQuantity = newValue.toInt()
            },
            valueRange = 0f..5f // Range of the slider
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuDrinkBases(drink: Drink?) {
    val context = LocalContext.current

    var isExpanded = remember {
        mutableStateOf(false)
    }
    var selectedText = remember {
        mutableStateOf("")
    }
    var selectedOptionIndex = remember {
        mutableIntStateOf(0)
    }

    val drinkBases = context.resources.getStringArray(R.array.drink_bases)

    var drinkIndex = drink?.baseDrink ?: -1
    if (drinkIndex == -1) {
        return
    }

    selectedOptionIndex.intValue = drinkIndex

    ExposedDropdownMenuBox(
        expanded = isExpanded.value,
        onExpandedChange = { isExpanded.value = !isExpanded.value },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TextField(
            value = drinkBases[selectedOptionIndex.intValue],
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = { isExpanded.value = false }
        ) {
            drinkBases.forEachIndexed { index, baseName ->
                DropdownMenuItem(
                    text = {
                        Text(text = baseName)
                    },
                    onClick = {
                        selectedText.value = baseName
                        selectedOptionIndex.value = index
                        isExpanded.value = false

                        drink?.baseDrink = index
                    }
                )
            }
        }
    }
}

@Composable
fun TitleTextTitle(text: String, bold: Boolean = false) {
    Text(
        text = text,
        fontSize = 24.sp,
        modifier = Modifier.padding(bottom = 16.dp),
        fontWeight = if(bold) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
fun TitleTextSubtitle(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

fun incrementIngredient(
    newDrink: Drink,
    ingredient: Int,
    ingredientsState: SnapshotStateList<Pair<Int, Int>>
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
    ingredient: Int,
    ingredientsState: SnapshotStateList<Pair<Int, Int>>
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