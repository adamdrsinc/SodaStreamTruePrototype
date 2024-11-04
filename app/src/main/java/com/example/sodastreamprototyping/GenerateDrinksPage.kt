package com.example.sodastreamprototyping

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sodastreamprototyping.viewModel.GenerateDrinksViewModel


@Composable

fun GenerateDrinksPage() {
    val viewModel : GenerateDrinksViewModel = hiltViewModel()
    Log.d("drink", viewModel.makeRandDrink().getUsedFlavors().toString())
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        Modifier.padding(10.dp)
    ) {
        items(100){ item->
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .padding(5.dp)
                    .clickable(){

                    }){
                Text("Sprite$item")
                Text("Durian")
                Text("Durian")
                Text("Durian")
            }
        }
    }
}



