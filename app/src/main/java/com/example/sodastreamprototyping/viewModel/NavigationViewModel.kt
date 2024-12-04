package com.example.sodastreamprototyping.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.sodastreamprototyping.Drink
import com.example.sodastreamprototyping.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    var selectedDrink: Drink by mutableStateOf(Drink(name= "New Drink", baseDrink = 0))
}

