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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sodastreamprototyping.ui.theme.SodaStreamPrototypingTheme

class BasketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val TEMP_DRINK_INGREDIENTS = listOf<String>(
            "Coke",
            "Dr. Pepper",
            "Sprite",
            "7-up",
            "A & W"
        )


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SodaStreamPrototypingTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    ShoppingBasket()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SodaStreamProtoPreview() {
    SodaStreamPrototypingTheme() {
        ShoppingBasket()
    }
}

@Composable
fun ShoppingBasket(modifier: Modifier = Modifier){
    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp

    MainLayout { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            BasketItems()

            CheckoutButton()

            /*Row(
            ){
                CheckoutButton()
                //BasketBackButton()
            }*/
        }
    }

}

/*@Composable
fun BasketBackButton(){
    val context = LocalContext.current

    Button(
        onClick = {

            val currentActivity = context as Activity
            val intent = Intent(currentActivity, com.example.sodastreamprototyping.MenuPage::class.java)
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
}*/

@Composable
fun CheckoutButton(){
    val context = LocalContext.current
    Button(
        onClick = {
            val activity = context as Activity
            val intent = Intent(activity, CheckoutActivity::class.java)
            activity.startActivity(intent)
        }
    ){
        Text(
            text = "Checkout"
        )
    }
}

@Composable
fun BasketItems(){
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp
    val screenHeight = config.screenHeightDp
    val context = LocalContext.current
    val currentActivity = context as Activity


    LazyColumn(
        modifier = Modifier
            .height((screenHeight * 0.80).dp)
            .width(screenWidth.dp)
    ){
        itemsIndexed(Basket.getDrinks()){
                id, item ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .width((screenWidth * 0.90).dp)
                    .border(2.dp, Color.Black)
                    .padding(24.dp)
            ){
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                if(item.baseDrink != null){
                    Text(
                        text = "Base: ${item.baseDrink.toString()}",
                        fontSize = 16.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp)
                        .width((screenWidth * 0.60).dp)){
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width((screenWidth * 0.40).dp)
                    ){
                        Text(
                            text = "Quantity:"
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width((screenWidth * 0.40).dp)
                    ){
                        Text(
                            text = item.quantity.toString(),
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp)
                        .width((screenWidth * 0.60).dp)){
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width((screenWidth * 0.40).dp)
                    ){
                        Text(
                            text = "Ice:"
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width((screenWidth * 0.40).dp)
                    ){
                        Text(
                            text = item.iceQuantity.toString(),
                        )
                    }
                }


                item.ingredients.forEach {
                        ingredient ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .width((screenWidth * 0.60).dp))
                    {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .width((screenWidth * 0.40).dp)
                        ){
                            Text(
                                text = "${ingredient.first}:"
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .width((screenWidth * 0.40).dp)
                        ){
                            Text(
                                text = ingredient.second.toString(),
                            )
                        }
                    }
                }

                Row(){
                    Button(
                        onClick = {
                            val intent = Intent(currentActivity, com.example.sodastreamprototyping.EditDrinkActivity::class.java)
                            intent.putExtra("drinkID", item.drinkID)
                            currentActivity.startActivity(intent)
                        },
                        modifier = Modifier
                            .padding(4.dp)
                    ){
                        Text(text = "Edit")
                    }

                    Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .padding(4.dp)
                    ){
                        Text(text = "Delete")
                    }
                }
            }


        }

    }
}
