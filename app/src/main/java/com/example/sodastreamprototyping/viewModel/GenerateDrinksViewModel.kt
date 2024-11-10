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
     * removes all drinks from list of ai generated drinks for a fresh start
     */
    fun clear(){
        _drinks.value = List(ai.baseSize){
            emptyList()
        }
    }

    /**
     * adds another unique AI generated drink to the [base]'s list. Gives up if the AI is not able to make a unique
     * drink in a limited number of attempts
     */
    fun expand(base: Int){
        var newDrink = makeRandDrink()
        val attempts = 30

        repeat(attempts){
            if(!drinks.value[base].contains(newDrink)){
                addDrink(base, newDrink)
                return
            }
            newDrink = makeRandDrink()
        }
    }

    /**
     * Ai will add flavors to the [recipe] until it thinks it is complete. If no recipe is provided, a base will be
     * chosen at random, starting a new recipe. Returns the completed recipe.
     */
    private fun makeRandDrink(recipe: Recipe? = null): Recipe {
        if (recipe == null) {
            val base = Random.nextInt(0, ai.baseSize)
            return makeRandDrink(Recipe(base))
        }


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
     * adds [drink] to the [base]'s list of available drinks
     */
    private fun addDrink(base: Int, drink: Recipe){
        _drinks.value = List(_drinks.value.size){
            if(it == base){
                drinks.value[it] + drink
            }
            else{
                drinks.value[it]
            }
        }
    }
}
