package com.example.sodastreamprototyping

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sodastreamprototyping.viewModel.GenerateDrinksViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateDrinksPage(onDrinkSelected: (Drink) -> Unit) {
    val viewModel : GenerateDrinksViewModel = hiltViewModel()
    val drinks by viewModel.drinks.collectAsState()
    val baseArray = LocalContext.current.resources.getStringArray(R.array.drink_bases)
    val flavorArray = LocalContext.current.resources.getStringArray(R.array.drink_flavors)
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshScope = CoroutineScope(Job() + Dispatchers.Main)

    PullToRefreshBox(isRefreshing = isRefreshing,
        onRefresh = {
            refreshScope.launch{
                isRefreshing = true
                viewModel.clear()
                delay(100) //due to a bug in the PullToRefreshBox, a delay must be added otherwise the icon won't go away
                isRefreshing = false
            }
        }, ) {


        LazyColumn {
            Modifier.padding(10.dp)
            items(baseArray.size) { base ->
                Text(
                    baseArray[base],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                LazyRow {
                    items(drinks[base]) { recipe ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .width(180.dp)
                                .height(220.dp)
                                .padding(5.dp)
                                .clickable(){
                                    onDrinkSelected(recipe)
                                }
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                Text(baseArray[recipe.baseDrink], fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                for (flavor in recipe.ingredients) {
                                    Text(flavor.second.toString() + "X " + flavorArray[flavor.first])
                                }
                            }
                        }
                    }
                    item {
                        if (drinks[base].size < 100 && viewModel.exhaustedDrinks[base] == false) {
                            CircularProgressIndicator()

                            LaunchedEffect(drinks) {
                                //This will only be called if the user has scrolled to the end of the list AND
                                // the drink list has been appended since the last time they reached the bottom
                                viewModel.expand(base)
                            }
                        }
                    }
                }
            }
        }
    }

}



