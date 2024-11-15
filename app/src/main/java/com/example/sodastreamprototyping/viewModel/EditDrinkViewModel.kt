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
        if(_drink.value.addIngredient(ingredientIndex)){
            _drink.value = _drink.value.copy() //seems redundant, but triggers a refresh
            return true
        }
        return false
    }

    fun removeIngredient(ingredientIndex: Int): Boolean{
        if(_drink.value.decrementIngredient(ingredientIndex)){
            _drink.value = _drink.value.copy()
            return true
        }
        return false
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