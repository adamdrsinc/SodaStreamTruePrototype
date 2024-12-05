package com.example.sodastreamprototyping

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sodastreamprototyping.viewModel.EditDrinkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDrinkPage(navController: NavController, drink: Drink) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val editDrinkViewModel =
        hiltViewModel<EditDrinkViewModel, EditDrinkViewModel.Factory>(creationCallback = { factory ->
            factory.create(drink = drink.copy())
        })
    val newDrink by editDrinkViewModel.drink.collectAsState()
    val suggestions by editDrinkViewModel.suggestion.collectAsState()

    var buttonText = "Save Changes"
    var titleText: String = "Edit Drink"

    if (drink.drinkID == null) {
        titleText = "New Drink"
        buttonText = "Add Drink"
    }

    //TODO: Get drink flavors from DB, not from resources
    val drinkFlavors =
        if (Repository.drinkFlavorsFromDB.isEmpty()) context.resources.getStringArray(R.array.drink_flavors)
        else Repository.drinkFlavorsFromDB.toTypedArray()

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

            // Display current selections
            SectionTitle("Current Drink Summary")
            CurrentDrinkSummary(newDrink,
                { editDrinkViewModel.addIngredient(it) },
                { editDrinkViewModel.removeIngredient(it) })
            Spacer(modifier = Modifier.height(18.dp))

            //Drink Bases Dropdown
            SectionTitle("Bases")
            DropdownMenuDrinkBases(newDrink) { editDrinkViewModel.setBase(it) }
            Spacer(modifier = Modifier.height(18.dp))

            // Accordion for ingredients
            SectionTitle("Ingredients")
            AccordionSectionIngredientRow(title = "Ingredients",
                items = drinkFlavors,
                suggestions,
                newDrink.getAllIngredientQuantity(drinkFlavors.size),
                { editDrinkViewModel.addIngredient(it) },
                { editDrinkViewModel.removeIngredient(it) })


            Spacer(modifier = Modifier.height(18.dp))

            SectionTitle("Ice Quantity")
            IceQuantitySlider(newDrink) { editDrinkViewModel.setIce(it) }
            Spacer(modifier = Modifier.height(18.dp))

            // TextField to change the drink name
            SectionTitle("Drink Name")
            TextField(value = newDrink.name, onValueChange = {
                editDrinkViewModel.setName(it)
            }, label = { Text("Drink Name") }, modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))

            // Save button
            Button(
                onClick = {
                    editDrinkViewModel.saveDrink()
                    navController.popBackStack()
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun SectionTitle(
    text: String, color: Color = Color.hsl(hue = 270f, saturation = 0.5f, lightness = 0.8f), centered: Boolean = false
) {
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
    suggestions: List<Int>,
    quantities: IntArray,
    increment: (Int) -> Boolean,
    decrement: (Int) -> Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { expanded = !expanded }) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = title, fontSize = 20.sp, modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        AnimatedVisibility(
            visible = expanded, enter = expandVertically(), exit = shrinkVertically()
        ) {
            if (!items.isEmpty()) {
                Column {
                    items.forEachIndexed { index, item ->
                        IngredientRow(
                            ingredient = Pair(index, item),
                            suggestions.contains(index),
                            quantities[index],
                            increment,
                            decrement
                        )
                    }
                }
            } else {
                Column {
                    Text(
                        text = "Ingredients could not be retrieved. Refresh the app.", textAlign = TextAlign.Center
                    )
                }

            }
        }
    }
}

@Composable
fun IngredientRow(
    ingredient: Pair<Int, String>,
    aiRecommended: Boolean = false,
    quantity: Int,
    increment: (Int) -> Boolean,
    decrement: (Int) -> Boolean
) {
    val context = LocalContext.current


    val modifier = Modifier
        .background(Color.Gray.copy(alpha = 0.25f))
//        .background(if (aiRecommended) Color.Blue.copy(alpha = 0.25f) else Color.Transparent)
        .fillMaxWidth()
        .clickable {
            if (!increment(ingredient.first)) {
                Toast
                    .makeText(context, "Syrup count cannot exceed ${Drink.MAX_PUMP_COUNT}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        .padding(16.dp)

    Row(
        modifier = modifier,
    ) {
        Box() {
            Text(
                text = ingredient.second, fontSize = 16.sp, modifier = Modifier.padding(end = 20.dp, top = 8.dp)
            )
            if (aiRecommended) {
//                Text("✨", fontSize = 16.sp, modifier = Modifier.align(Alignment.TopEnd))
                Image(
                    painter = painterResource(R.drawable.baseline_star_24),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }


        if (quantity > 0) {
//            Spacer(modifier = Modifier.fillMaxWidth())
            Box(Modifier.fillMaxWidth()) {
                Row(Modifier.align(Alignment.TopEnd)) {

                    IconButton(onClick = {
                        if (!decrement(ingredient.first)) {
                            Toast.makeText(
                                context, "Syrup count cannot ${Drink.MAX_PUMP_COUNT}", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text(
                            text = "-",
                            fontSize = 18.sp,
                            color = Color.Red,
//                        modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    // Quantity Display
                    Text(
                        text = quantity.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.CenterVertically)
                    )

                    // Increment Button
                    IconButton(onClick = {
                        if (!increment(ingredient.first)) {
                            Toast.makeText(
                                context, "Syrup count cannot exceed ${Drink.MAX_PUMP_COUNT}", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text(text = "+", fontSize = 18.sp, color = Color.Green)
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentDrinkSummary(drink: Drink, increment: (Int) -> Boolean, decrement: (Int) -> Boolean) {
    val context = LocalContext.current
    val drinkFlavors = context.resources.getStringArray(R.array.drink_flavors)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Current Drink: ${drink.name}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (drink.ingredients.isEmpty()) {
            Text(
                text = "No ingredients added yet.", fontSize = 16.sp, color = Color.Gray
            )
        } else {
            drink.ingredients.forEachIndexed { index, (ingredient, quantity) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = drinkFlavors[ingredient], fontSize = 16.sp, modifier = Modifier.weight(1f)
                    )

                    // Decrement Button
                    IconButton(onClick = {
                        if (!decrement(ingredient)) {
                            Toast.makeText(
                                context, "Syrup count cannot ${Drink.MAX_PUMP_COUNT}", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
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
                    IconButton(onClick = {
                        if (!increment(ingredient)) {
                            Toast.makeText(
                                context, "Syrup count cannot exceed ${Drink.MAX_PUMP_COUNT}", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text(text = "+", fontSize = 18.sp, color = Color.Green)
                    }
                }
            }
        }
    }
}

@Composable
fun IceQuantitySlider(drink: Drink, setIce: (Int) -> Unit) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    // State to hold the slider position, can't be stored in drink since this is a float
    val sliderPosition = remember { mutableFloatStateOf(drink.iceQuantity.toFloat()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Text to display the current value of the slider
        Text(text = "Ice amount: ${sliderPosition.floatValue.toInt()}")

        // Slider
        Slider(
            value = sliderPosition.floatValue, onValueChange = { newValue ->
                sliderPosition.floatValue = newValue
                setIce(newValue.toInt())
            }, valueRange = 0f..5f // Range of the slider
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuDrinkBases(newDrink: Drink, selectBase: (Int) -> Unit) {
    val context = LocalContext.current

    var isExpanded by remember { mutableStateOf(false) }
    val drinkBases = context.resources.getStringArray(R.array.drink_bases)

    ExposedDropdownMenuBox(
        expanded = isExpanded, onExpandedChange = { isExpanded = !isExpanded }, modifier = Modifier.fillMaxWidth()
    ) {
        TextField(value = drinkBases[newDrink.baseDrink],
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor() // Ensure the TextField acts as an anchor for the dropdown
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            drinkBases.forEachIndexed { index, baseName ->
                DropdownMenuItem(text = {
                    Text(text = baseName)
                }, onClick = {
                    selectBase(index)
                    isExpanded = false
                })
            }
        }
    }
}