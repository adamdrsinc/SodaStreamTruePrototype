package com.example.sodastreamprototyping

import androidx.compose.runtime.snapshots.SnapshotStateList

class Drink(
    var ingredients: ArrayList<Pair<String, Int>> = ArrayList(),
    var name: String,
    var price: Double = 0.00,
    var quantity: Int = 1,
    var iceQuantity: Int = 0,
    val isCustom: Boolean = false,
    var baseDrink: String? = null,
    var description: String? = null,
    var drinkID: Int? = null
) {

    private val maxPumpCountForCollectiveIngredients = 5
    private var currentPumpCountForCollectiveIngredient = 0

    init{
        for(drinkIngredient in ingredients){
            currentPumpCountForCollectiveIngredient += drinkIngredient.second
        }
    }

    private fun getIngredientPair(ingredientName: String): Pair<String, Int>?{

        for(i in 0 until ingredients.size){
            if(ingredients[i].first == ingredientName)
                return ingredients[i]
        }

        return null
    }

    fun decrementIngredient(ingredient: String): Boolean {
        var currentIngredient = getIngredientPair(ingredient)

        if (currentIngredient == null) return false


        // Iterate through the list backward to prevent index shift issues when modifying
        for (i in ingredients.indices.reversed()) {
            if (ingredients[i].first == ingredient) {
                ingredients[i] = ingredients[i].copy(second = ingredients[i].second - 1)
                currentPumpCountForCollectiveIngredient -= 1
                break
            }
        }

        return true
    }

    fun incrementIngredient(ingredient: String): Boolean{
        var currentIngredient = getIngredientPair(ingredient)

        if(currentIngredient == null)
            return false


        if(currentPumpCountForCollectiveIngredient == maxPumpCountForCollectiveIngredients)
            return false

        for(i in 0 until ingredients.size){
            if(ingredients[i].first == ingredient){
                ingredients[i] = ingredients[i].copy(second = ingredients[i].second + 1)
                currentPumpCountForCollectiveIngredient += 1
                break
            }
        }

        return true
    }


}