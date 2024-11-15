package com.example.sodastreamprototyping.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sodastreamprototyping.Basket
import com.example.sodastreamprototyping.Drink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditDrinkViewModel(startDrink: Drink): ViewModel() {
    private val _drink = MutableStateFlow(startDrink.copy())
    val drink : StateFlow<Drink> = _drink.asStateFlow()

    fun setName(name: String){
        _drink.value = _drink.value.copy(name=name)
    }

    fun setBase(baseIndex: Int){
        _drink.value = _drink.value.copy(baseDrink = baseIndex)
    }

    fun setIce(quantity: Int){
        _drink.value = _drink.value.copy(iceQuantity = quantity)
    }

    fun addIngredient(ingredientIndex: Int): Boolean{
        return _drink.value.addIngredient(ingredientIndex)
    }

    fun removeIngredient(ingredientIndex: Int): Boolean{
        return _drink.value.decrementIngredient(ingredientIndex)
    }

    fun saveDrink(){
        Basket.saveDrink(_drink.value)
    }

    class Factory(val drink: Drink) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EditDrinkViewModel(drink) as T
        }

    }
}