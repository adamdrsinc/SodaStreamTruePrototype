package com.example.sodastreamprototyping.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.sodastreamprototyping.Drink

class NavigationViewModel: ViewModel() {

    var selectedDrink: Drink by mutableStateOf(Drink(name= "New Drink", baseDrink = 0))

}