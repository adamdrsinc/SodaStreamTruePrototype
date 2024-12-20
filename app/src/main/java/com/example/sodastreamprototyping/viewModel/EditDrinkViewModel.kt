package com.example.sodastreamprototyping.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sodastreamprototyping.Basket
import com.example.sodastreamprototyping.Drink
import com.example.sodastreamprototyping.Repository
import com.example.sodastreamprototyping.TensorFlowAPI
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditDrinkViewModel.Factory::class)
class EditDrinkViewModel @AssistedInject constructor(
    private val ai: TensorFlowAPI,
    private val repo: Repository,
    @Assisted startDrink: Drink,
) : ViewModel() {

    @AssistedFactory interface Factory {
        fun create(drink: Drink): EditDrinkViewModel
    }

    private val _drink = MutableStateFlow(startDrink.copy())
    private val _suggestions = MutableStateFlow<List<Int>>(emptyList())

    val drink: StateFlow<Drink> = _drink.asStateFlow()
    val suggestion: StateFlow<List<Int>> = _suggestions.asStateFlow()
    val bases = repo.basesFromDB.asStateFlow()
    val flavors = repo.drinkFlavorsFromDB.asStateFlow()

    init {
        regenerateSuggestions()
    }

    fun setName(name: String) {
        _drink.value = _drink.value.copy(name = name)
    }

    fun setIce(quantity: Int) {
        _drink.value = _drink.value.copy(iceQuantity = quantity)
    }

    fun setBase(baseIndex: Int) {
        _drink.value = _drink.value.copy(baseDrink = baseIndex)
        regenerateSuggestions()
    }

    fun addIngredient(ingredientIndex: Int): Boolean {
        val success = _drink.value.addIngredient(ingredientIndex)
        regenerateSuggestions()
        return success

    }

    fun removeIngredient(ingredientIndex: Int): Boolean {
        val success =  _drink.value.decrementIngredient(ingredientIndex)
        regenerateSuggestions()
        return success
    }

    fun saveDrink() {
        Basket.saveDrink(_drink.value)
    }

    private fun regenerateSuggestions() {
        _suggestions.value = emptyList<Int>() //generation may take a few seconds, don't mislead the user by showing
        // old suggestions
        viewModelScope.launch{
            _suggestions.value =
                ai.generateDrink(_drink.value.baseDrink, _drink.value.getAllIngredientQuantity(ai.flavorSize))
        }
    }


}