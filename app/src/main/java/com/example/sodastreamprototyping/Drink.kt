package com.example.sodastreamprototyping

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Drink(
    val ingredients: SnapshotStateList<Pair<String, Int>> = mutableStateListOf(),
    var name: String,
    var price: Double = 0.00,
    var quantity: Int = 1,
    var iceQuantity: Int = 0,
    val isCustom: Boolean = false,
    var baseDrink: String = "Cola",
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
    }

    fun hasIngredient(ingredient: String): Pair<String, Int>? {
        return ingredients.find { it.first == ingredient }
    }

    fun addIngredient(ingredient: String): Boolean {
        if (currentPumpCount < MAX_PUMP_COUNT) {
            ingredients.add(Pair(ingredient, 1))
            currentPumpCount++
            return true
        }
        return false
    }

    fun incrementIngredient(ingredient: String): Boolean {
        val existingIngredient = hasIngredient(ingredient)
        if (existingIngredient != null && currentPumpCount < MAX_PUMP_COUNT) {
            val index = ingredients.indexOf(existingIngredient)
            ingredients[index] = existingIngredient.copy(second = existingIngredient.second + 1)
            currentPumpCount++
            return true
        }
        return false
    }

    fun decrementIngredient(ingredient: String): Boolean {
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

/*
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

    private var currentPumpCount = 0

    init{
        for(drinkIngredient in ingredients){
            currentPumpCount += drinkIngredient.second
        }
    }

    companion object{
        const val MAX_PUMP_COUNT = 5
    }

    fun hasIngredient(ingredient: String): Pair<String, Int>?{

        val ingredientIndex = ingredients.indexOfFirst {
            it.first == ingredient
        }

        return if(ingredientIndex != -1){
            ingredients[ingredientIndex]
        }else{
            null
        }

        return null
    }

    fun decrementIngredient(ingredient: String): Boolean {
        var currentIngredient = hasIngredient(ingredient)

        if (currentIngredient == null) return false


        // Iterate through the list backward to prevent index shift issues when modifying
        for (i in ingredients.indices.reversed()) {
            if (ingredients[i].first == ingredient) {
                ingredients[i] = ingredients[i].copy(second = ingredients[i].second - 1)
                currentPumpCount -= 1
                break
            }
        }



        return true
    }

    fun incrementIngredient(ingredient: String): Boolean{
        var currentIngredient = hasIngredient(ingredient)

        if(currentIngredient == null)
            return false


        if(currentPumpCount == MAX_PUMP_COUNT)
            return false


        val ingredientIndex = ingredients.indexOfFirst {
            it.first == ingredient
        }

        if(ingredientIndex != -1){
            ingredients[ingredientIndex] =
                ingredients[ingredientIndex].copy(second = ingredients[ingredientIndex].second + 1)
            currentPumpCount++
            return true
        }else{
            return false
        }

        return false
    }

    fun addIngredient(ingredient: String): Boolean{
        var currentIngredient = hasIngredient(ingredient)

        if(currentPumpCount == MAX_PUMP_COUNT){
            return false
        }

        if(currentIngredient == null) {
            ingredients.add(Pair<String, Int>(ingredient, 1))
            currentPumpCount++
            return true
        }

        return false
    }


}*/
