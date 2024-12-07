package com.example.sodastreamprototyping

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MyDrinksPage(navController: NavController, onCreateDrink: (Drink) -> Unit) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(bottom = 56.dp) // Add padding to avoid overlap with the button
        ) {
            DemoCode.drinksDemoList.forEach { drink ->
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