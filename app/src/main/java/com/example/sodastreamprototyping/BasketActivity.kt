package com.example.sodastreamprototyping

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sodastreamprototyping.ui.theme.SodaStreamPrototypingTheme


@Composable
fun ShoppingBasket(navController: NavController, modifier: Modifier = Modifier){
    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp

    MainLayout(navController = navController) {innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            BasketItems(navController)

            CheckoutButton(navController)
        }
    }

}


@Composable
fun CheckoutButton(navController: NavController){
    Button(
        onClick = {
            navController.navigate("checkout")
        }
    ){
        Text(
            text = "Checkout"
        )
    }
}

@Composable
fun BasketItems(navController: NavController) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val screenHeight = config.screenHeightDp

    val drinks = remember {
        mutableStateListOf<Drink>().apply { addAll(Basket.basketDrinks) }
    }

    LazyColumn(
        modifier = Modifier
            .height((screenHeight * 0.80).dp)
            .width(screenWidth.dp)
    ) {
        itemsIndexed(drinks) { index, drink ->
            BasketItemCard(
                drink = drink,
                screenWidth = screenWidth,
                onEdit = { navController.navigate(Screen.Edit.withArgs(drink.drinkID.toString())) },
                onDelete = {
                    Basket.basketDrinks.removeAt(index)
                    drinks.removeAt(index)
                }
            )
        }
    }
}

@Composable
fun BasketItemCard(
    drink: Drink,
    screenWidth: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
            .width((screenWidth * 0.90).dp)
            .border(2.dp, Color.Black)
            .padding(24.dp)
    ) {
        Text(
            text = drink.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        drink.baseDrink?.let {
            Text(text = "Base: $it", fontSize = 16.sp)
        }

        DrinkRow("Quantity", drink.quantity.toString(), screenWidth)
        DrinkRow("Ice", drink.iceQuantity.toString(), screenWidth)

        drink.ingredients.forEach { ingredient ->
            DrinkRow(ingredient.first, ingredient.second.toString(), screenWidth)
        }

        ActionButtons(isCustom = drink.isCustom, onEdit = onEdit, onDelete = onDelete)
    }
}

@Composable
fun DrinkRow(label: String, value: String, screenWidth: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .width((screenWidth * 0.60).dp)
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.width((screenWidth * 0.40).dp),
            textAlign = TextAlign.Start
        )
        Text(
            text = value,
            modifier = Modifier.width((screenWidth * 0.40).dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ActionButtons(isCustom: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row {
        if(isCustom){
            Button(onClick = onEdit, modifier = Modifier.padding(4.dp)) {
                Text(text = "Edit")
            }
        }

        Button(onClick = onDelete, modifier = Modifier.padding(4.dp)) {
            Text(text = "Delete")
        }
    }
}

