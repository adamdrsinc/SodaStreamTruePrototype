package com.example.sodastreamprototyping.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sodastreamprototyping.TensorFlowAPI
import com.example.sodastreamprototyping.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GenerateDrinksViewModel @Inject constructor(private val ai: TensorFlowAPI): ViewModel() {
    private val _drinks = MutableStateFlow<List<List<Recipe>>>(emptyList())
    val drinks = _drinks.asStateFlow()

    init{
        viewModelScope.launch{
            _drinks.value = List(ai.baseSize){ base ->
                List(1){
                   makeRandDrink(Recipe(base))
                }
            }
        }
    }

    /**
     *
     */
    fun makeRandDrink(recipe: Recipe? = null): Recipe {
        if (recipe == null) {
            val base = Random.nextInt(0, ai.baseSize)
            return makeRandDrink(Recipe(base))
        }


        //val available flavors = AI.predict(recipe)
        val options = ai.generateDrink(recipe.base, recipe.flavors.toIntArray())
        if(options.isEmpty()){
            return recipe
        }
        val chosenFlavor = options.random()
        if(chosenFlavor == ai.endDrinkIndex){
            return recipe
        }
        recipe.addFlavor(chosenFlavor)
        return makeRandDrink(recipe)
    }

    /**
     * adds another AI generated drink to the [base]'s list
     */
    fun expand(base: Int){
        _drinks.value = List(_drinks.value.size){
            if(it == base){
                drinks.value[it] + makeRandDrink(Recipe(base))
            }
            else{
                drinks.value[it]
            }
        }
    }
}
