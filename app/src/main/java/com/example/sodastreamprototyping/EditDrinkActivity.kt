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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sodastreamprototyping.viewModel.EditDrinkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDrinkPage(navController: NavController, drink: Drink) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val editDrinkViewModel : EditDrinkViewModel = viewModel(factory = EditDrinkViewModel.Factory(drink.copy()))
    val newDrink by editDrinkViewModel.drink.collectAsState()

    var buttonText = "Save Changes"
    var titleText: String = "Edit Drink"

    if(drink.drinkID == null){
        titleText = "New Drink"
        buttonText = "Add Drink"
    }

    //TODO: Get drink flavors from DB, not from resources
    val drinkFlavors = context.resources.getStringArray(R.array.drink_flavors)

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
            //Title
            TitleText(titleText)

            //Drink Bases Dropdown
            TitleText("Bases")
            DropdownMenuDrinkBases(newDrink){editDrinkViewModel.setBase(it)}
            Spacer(modifier = Modifier.height(16.dp))

            // Accordion for ingredients
            TitleText("Ingredients")
            AccordionSectionIngredientRow(title = "Ingredients", items = drinkFlavors){
                editDrinkViewModel.addIngredient(it)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TextField to change the drink name
            TitleText("Drink Name")
            TextField(
                value = newDrink.name,
                onValueChange = {
                    editDrinkViewModel.setName(it)
                },
                label = { Text("Drink Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display current selections
            CurrentDrinkSummary(newDrink,
                {editDrinkViewModel.addIngredient(it)}, {editDrinkViewModel.removeIngredient(it)})
            Spacer(modifier = Modifier.height(16.dp))

            IceQuantitySlider(newDrink){editDrinkViewModel.setIce(it)}
            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = {
                    editDrinkViewModel.saveDrink()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun AccordionSectionIngredientRow(title: String, items: Array<String>, selectFlavor: (Int) -> Boolean) {
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
                items.forEachIndexed { index, item ->
                    IngredientRow(ingredient = Pair(index, item), false, selectFlavor)
                }
            }
        }
    }
}

@Composable
fun IngredientRow(ingredient: Pair<Int, String>, aiRecommended: Boolean = false, select: (Int) -> Boolean) {
    val context = LocalContext.current

    val modifier = Modifier
        .background(if (aiRecommended) Color.Yellow else Color.Transparent)
        .fillMaxWidth()
        .clickable {
            if (!select(ingredient.first) ) {
                Toast.makeText(context, "Syrup count cannot exceed ${Drink.MAX_PUMP_COUNT}", Toast.LENGTH_SHORT).show()
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
fun CurrentDrinkSummary(drink: Drink, increment: (Int) -> Boolean, decrement: (Int)-> Boolean) {
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
                text = "No ingredients added yet.",
                fontSize = 16.sp,
                color = Color.Gray
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
                        text = drinkFlavors[ingredient],
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    // Decrement Button
                    IconButton(
                        onClick = {
                            if (!decrement(ingredient)) {
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
                            if (!increment(ingredient)) {
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
fun IceQuantitySlider(drink: Drink, setIce:(Int)-> Unit) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    // State to hold the slider position, can't be stored in drink since this is a float
    val sliderPosition = remember { mutableFloatStateOf(drink.iceQuantity.toFloat()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width((screenWidth * 0.80).dp)
            .padding(16.dp)
    ) {
        // Text to display the current value of the slider
        Text(text = "Ice amount: ${sliderPosition.floatValue.toInt()}")

        // Slider
        Slider(
            value = sliderPosition.floatValue,
            onValueChange = { newValue ->
                sliderPosition.floatValue = newValue
                setIce(newValue.toInt())
            },
            valueRange = 0f..5f // Range of the slider
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuDrinkBases(newDrink: Drink, selectBase: (Int) -> Unit){
    val context = LocalContext.current

    var isExpanded = remember{
        mutableStateOf(false)
    }
    var selectedText = remember{
        mutableStateOf("")
    }
    val drinkBases = context.resources.getStringArray(R.array.drink_bases)

    ExposedDropdownMenuBox(
        expanded = isExpanded.value,
        onExpandedChange = {isExpanded.value = !isExpanded.value},
    ) {
        TextField(
            value = drinkBases[newDrink.baseDrink],
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = {isExpanded.value = false},
        ) {
            drinkBases.forEachIndexed {
                    index, baseName ->
                DropdownMenuItem(
                    text = {
                        Text(text = baseName)
                    },
                    onClick = {
                        selectedText.value = baseName
                        selectBase(index)
                        isExpanded.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun TitleText(text:  String){
    Text(
        text = text,
        fontSize = 24.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}