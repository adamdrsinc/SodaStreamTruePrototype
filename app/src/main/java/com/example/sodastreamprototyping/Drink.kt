package com.example.sodastreamprototyping

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Drink(
    val ingredients: SnapshotStateList<Pair<Int, Int>> = mutableStateListOf(),
    var name: String,
    var price: Double = 0.00,
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
    }

    init {
        for (drinkIngredient in ingredients) {
            currentPumpCount += drinkIngredient.second
        }

        drinkID = Basket.getNextDrinkID()
    }

    fun hasIngredient(ingredient: Int): Pair<Int, Int>? {
        return ingredients.find { it.first == ingredient }
    }

    fun addIngredient(ingredient: Int): Boolean {
        if (currentPumpCount < MAX_PUMP_COUNT) {
            ingredients.add(Pair(ingredient, 1))
            currentPumpCount++
            return true
        }
        return false
    }

    fun incrementIngredient(ingredient: Int): Boolean {
        val existingIngredient = hasIngredient(ingredient)
        if (existingIngredient != null && currentPumpCount < MAX_PUMP_COUNT) {
            val index = ingredients.indexOf(existingIngredient)
            ingredients[index] = existingIngredient.copy(second = existingIngredient.second + 1)
            currentPumpCount++
            return true
        }
        return false
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
}