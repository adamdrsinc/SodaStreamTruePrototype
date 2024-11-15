package com.example.sodastreamprototyping

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

/**
 * takes in a list of drinks of [ingredients], where the first value in the pair is the ingredient index, and the second
 * is the quantity. The [name] will be used when displaying the drink to users. Other values can be customized, but are
 * provided by default.
 */
data class Drink(
    var ingredients: MutableList<Pair<Int, Int>> = mutableListOf<Pair<Int, Int>>(),
    var name: String,
    var quantity: Int = 1,
    var iceQuantity: Int = 0,
    val isCustom: Boolean = false,
    var baseDrink: Int = 0,
    var description: String? = null,
    var drinkID: Int? = null
) {
    var currentPumpCount = 0

    companion object {
        const val MAX_PUMP_COUNT = 5
        const val BASE_PRICE = 2.00
        const val INGREDIENT_COST = 0.10
        const val MAX_ICE = 5
    }

    init {
        //ingredients must be a mutable state in order for ui changes to occur, calling this also creates copy,
        // preventing two drinks from sharing the same list
        ingredients = ingredients.toMutableStateList()
        for (drinkIngredient in ingredients) {
            currentPumpCount += drinkIngredient.second
        }

    }

    fun hasIngredient(ingredient: Int): Pair<Int, Int>? {
        return ingredients.find { it.first == ingredient }
    }

    /**
     * Increments [ingredient] if it exists, or adds it to the list if it doesn't
     */
    fun addIngredient(ingredient: Int): Boolean {
        val existingIngredient = hasIngredient(ingredient)
        if (existingIngredient != null && currentPumpCount < MAX_PUMP_COUNT) {
            val index = ingredients.indexOf(existingIngredient)
            ingredients[index] = existingIngredient.copy(second = existingIngredient.second + 1)
            currentPumpCount++
            return true
        }
        return addNewIngredient(ingredient)
    }


    fun decrementIngredient(ingredient: Int): Boolean {
        val existingIngredient = hasIngredient(ingredient)
        if (existingIngredient != null && existingIngredient.second > 1) {
            val index = ingredients.indexOf(existingIngredient)
            ingredients[index] = existingIngredient.copy(second = existingIngredient.second - 1)
            currentPumpCount--
            return true
        } else if (existingIngredient != null && existingIngredient.second == 1) {
            currentPumpCount--
            ingredients.remove(existingIngredient)
            return true
        }
        return false
    }

    /**
     * calculates and returns the price of the drink. Using [BASE_PRICE] as the base, and charging [INGREDIENT_COST]
     * per ingredient in the drink.
     */
    fun getPrice(): Double{
        var price = BASE_PRICE
        for(ingredient in ingredients){
            price += INGREDIENT_COST * ingredient.second
        }
        return price
    }

    /**
     * Adds a new [ingredient] to the list of ingredients. For use with ingredients that haven't been added to the
     * drink, not incrementing.
     */
    private fun addNewIngredient(ingredient: Int): Boolean {
        if (currentPumpCount < MAX_PUMP_COUNT) {
            ingredients.add(Pair(ingredient, 1))
            currentPumpCount++
            return true
        }
        return false
    }

}