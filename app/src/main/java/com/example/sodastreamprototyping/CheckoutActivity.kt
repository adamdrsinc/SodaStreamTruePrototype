package com.example.sodastreamprototyping

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sodastreamprototyping.ui.theme.SodaStreamPrototypingTheme

/*
class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SodaStreamPrototypingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
*/

@Composable
fun Checkout(navController: NavController)
{
    MainLayout(navController) {
        innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            DrinksList()

        }

    }
}

@Composable
fun TotalCost(){
    var totalCost = 0.00
    Basket.basketDrinks.forEach{
        totalCost += it.price
    }

    Text(
        text = totalCost.toString()
    )
}

@Composable
fun DrinksList(){
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
            .width((screenWidth * 0.90).dp)
            .border(2.dp, Color.Black)
            .padding(24.dp)
    ){
        Basket.basketDrinks.forEachIndexed{
                index, drink ->
            Row(){

            }
        }
    }

}