package com.example.sodastreamprototyping

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sodastreamprototyping.viewModel.GenerateDrinksViewModel


@Composable

fun GenerateDrinksPage() {
    val viewModel : GenerateDrinksViewModel = hiltViewModel()
    val baseArray = LocalContext.current.resources.getStringArray(R.array.drink_bases)
    val flavorArray = LocalContext.current.resources.getStringArray(R.array.drink_flavors)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        Modifier.padding(10.dp)
    ) {
        items(100){ item->
            val recipe = viewModel.makeRandDrink()
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(180.dp)
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



