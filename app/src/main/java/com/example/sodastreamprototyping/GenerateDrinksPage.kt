package com.example.sodastreamprototyping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sodastreamprototyping.model.Recipe
import com.example.sodastreamprototyping.viewModel.GenerateDrinksViewModel


@Composable

fun GenerateDrinksPage() {
    val viewModel : GenerateDrinksViewModel = hiltViewModel()
    val baseArray = LocalContext.current.resources.getStringArray(R.array.drink_bases)
    val flavorArray = LocalContext.current.resources.getStringArray(R.array.drink_flavors)

    LazyColumn {
        Modifier.padding(10.dp)
        items(baseArray.size){ base->
            Text(baseArray[base], fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
            LazyRow {
                items(20){
                    val recipe = viewModel.makeRandDrink(Recipe(base))
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .width(180.dp)
                            .padding(5.dp)){
                        Column (Modifier.padding(8.dp)){
                            Text(baseArray[recipe.base], fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            for (flavor in recipe.getUsedFlavors()){
                                Text(flavor.second.toString() + "X " + flavorArray[flavor.first])
                            }
                        }
                    }
                }
            }
        }
    }

}



