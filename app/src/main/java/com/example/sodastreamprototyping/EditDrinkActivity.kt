package com.example.sodastreamprototyping

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sodastreamprototyping.ui.theme.SodaStreamPrototypingTheme

class EditDrinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val drinkID = intent.getIntExtra("drinkID", -1)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SodaStreamPrototypingTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    EditMenuComposable(drinkID = drinkID)
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SodaStreamProtoTheme {
    }
}
*/

@Composable
fun EditMenuComposable(drinkID: Int){
    val drink = Basket.getDrinks().find {
        it.drinkID == drinkID
    }
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val screenHeight = config.screenHeightDp


    MainLayout { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .height((screenHeight * 0.80).dp)
                .width((screenWidth * 0.80).dp))

        {

            TitleText(drink!!.name)

            if(drink.isCustom){
                CustomDrinkDropdown(drinkID)
            }

            Column(
                modifier = Modifier
                    .border(2.dp, Color.Black)
                    .padding(24.dp)
            ){
                TitleText("Syrups")
                ShowSyrupsList(drinkID)
            }

            Column(
                modifier = Modifier
                .border(2.dp, Color.Black)
                .padding(24.dp)
            ){
                TitleText("Ice")
                IceQuantitySlider(drinkID)
            }

            EditDrinkBackButton()
        }
    }

}

@Composable
fun ShowSyrupsList(drinkID: Int) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val screenHeight = config.screenHeightDp

    // Find the current drink by drinkID
    var currentDrink: Drink? = null
    for (i in 0 until Basket.getDrinks().size) {
        if (Basket.getDrinks()[i].drinkID == drinkID) {
            currentDrink = Basket.getDrinks()[i]
        }
    }

    // Create a state list to manage ingredient counts
    val ingredientCounts = remember {
        mutableStateListOf<Pair<String, Int>>().apply {
            currentDrink?.ingredients?.let { addAll(it) }
        }
    }

    Column {
        ingredientCounts.forEachIndexed { index, ingredientPair ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .height((screenHeight * 0.10).dp)
                    .width((screenWidth * 0.80).dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width((screenWidth * 0.40).dp)
                ) {
                    Text(text = "${ingredientPair.first}:")
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width((screenWidth * 0.40).dp)
                ) {
                    // Decrement button
                    Button(
                        onClick = {
                            if (currentDrink?.decrementIngredient(ingredientPair.first) == true) {
                                val updatedCount = ingredientPair.second - 1
                                if (updatedCount > 0) {
                                    // Update the count
                                    ingredientCounts[index] = ingredientPair.copy(second = updatedCount)
                                } else {
                                    // Remove the ingredient if the count is 0
                                    ingredientCounts.removeAt(index)
                                    currentDrink.ingredients.removeAt(index)
                                }
                            }
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(text = "-")
                    }

                    // Display count
                    Text(
                        text = "${ingredientPair.second}",
                        modifier = Modifier.padding(4.dp)
                    )

                    // Increment button
                    Button(
                        onClick = {
                            if (currentDrink?.incrementIngredient(ingredientPair.first) == true) {
                                // Update the count
                                ingredientCounts[index] = ingredientPair.copy(second = ingredientPair.second + 1)
                            } else {
                                Toast.makeText(context, "A total of 5 syrups may be added.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(text = "+")
                    }
                }
            }
        }
    }
}


@Composable
fun IceQuantitySlider(drinkID: Int) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val drink = Basket.getDrinks().find {
        it.drinkID == drinkID
    }
    if(drink == null)
        return

    // State to hold the slider position
    val sliderPosition = remember { mutableStateOf(drink.iceQuantity.toFloat()) } // 0f to 1f range

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width((screenWidth * 0.80).dp)
            .padding(16.dp)
    ) {
        // Text to display the current value of the slider
        Text(text = "Slider value: ${sliderPosition.value.toInt()}")

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


@Composable
fun TitleText(text: String){
    Text(
        text = text.uppercase(),
        fontSize = 24.sp,
        modifier = Modifier
            .padding(top = 16.dp, bottom = 16.dp)
    )

}

@Composable
fun EditDrinkBackButton(){
    val context = LocalContext.current

    Button(
        onClick = {

            val currentActivity = context as Activity
            val intent = Intent(currentActivity, com.example.sodastreamprototyping.BasketActivity::class.java)
            currentActivity.startActivity(intent)
        },
        modifier = Modifier
            .padding(16.dp)
    ){
        Text(
            text = "Back",
            fontSize = 12.sp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDrinkDropdown(drinkID: Int){
    var isExpanded = remember{
        mutableStateOf(false)
    }
    var selectedText = remember{
        mutableStateOf("")
    }
    var selectedOptionIndex = remember{
        mutableIntStateOf(0)
    }

    //TODO: Delete this later.
    val TEMP_DRINK_INGREDIENTS = listOf<String>(
        "Coke",
        "Dr. Pepper",
        "Sprite",
        "7-up",
        "A & W"
    )

    var theDrink: Drink? = null
    for(i in 0 until Basket.getDrinks().size){
        if(Basket.getDrinks()[i].drinkID == drinkID){
            theDrink = Basket.getDrinks()[i]
            break
        }
    }

    var drinkIndex: Int = 0
    for(i in 0 until TEMP_DRINK_INGREDIENTS.size){
        if(theDrink?.baseDrink == TEMP_DRINK_INGREDIENTS[i]){
            drinkIndex = i
            break
        }
    }

    selectedOptionIndex.intValue = drinkIndex

    ExposedDropdownMenuBox(
        expanded = isExpanded.value,
        onExpandedChange = {isExpanded.value = !isExpanded.value},
    ) {
        TextField(
            value = TEMP_DRINK_INGREDIENTS[selectedOptionIndex.intValue],
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
            TEMP_DRINK_INGREDIENTS.forEachIndexed {
                    index, baseName ->
                DropdownMenuItem(
                    text = {
                        Text(text = baseName)
                    },
                    onClick = {
                        selectedText.value = baseName
                        selectedOptionIndex.value = index
                        isExpanded.value = false

                        for(i in 0 until Basket.getDrinks().size){
                            if(Basket.getDrinks()[i].drinkID == drinkID){
                                Basket.getDrinks()[i].baseDrink = baseName
                                break
                            }
                        }
                    }
                )
            }
        }
    }


}

@Composable
fun DisplayDrinkID(drinkID: Int){
    Text(
        text = if(drinkID != -1) "Drink ID: $drinkID" else "Drink not valid"
    )
}