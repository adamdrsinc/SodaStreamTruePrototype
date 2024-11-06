package com.example.sodastreamprototyping.viewModel

import androidx.lifecycle.ViewModel
import com.example.sodastreamprototyping.TensorFlowAPI
import com.example.sodastreamprototyping.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GenerateDrinksViewModel @Inject constructor(private val ai: TensorFlowAPI): ViewModel() {
    /**
     * Procedurally generates a random drink. If a [recipe] is provided, the [recipe] will be completed by the AI,
     * otherwise, a random base drink is provided, and the drink is generated from there.
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
}
