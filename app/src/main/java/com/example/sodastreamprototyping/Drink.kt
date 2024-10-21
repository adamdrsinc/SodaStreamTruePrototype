package com.example.sodastreamprototyping

class Drink(
    var ingredients: ArrayList<Pair<String, Int>>,
    var name: String,
    var price: Double,
    var quantity: Int,
    var iceQuantity: Int = 0,
    val isCustom: Boolean,
    var baseDrink: String? = null,
    var description: String? = null
) {

    private val maxPumpCountForCollectiveIngredients = 5
    private var currentPumpCountForCollectiveIngredient = 0
    var drinkID: Int = 0

    init{
        drinkID =
            if(Basket.getDrinks().isNotEmpty()){
                Basket.getDrinks().last().drinkID + 1
            } else{
                1
            }

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

    fun addIngredient(newIngredient: String, quantity: Int) : Boolean{
        var ingredient = getIngredientPair(newIngredient)
        if(ingredient != null) return false

        ingredients.add(Pair(newIngredient, quantity))
        currentPumpCountForCollectiveIngredient += quantity
        return true
    }

    fun removeIngredient(ingredient: String): Boolean{
        var ingredient = getIngredientPair(ingredient)
        if(ingredient == null) return false

        ingredients.remove(ingredient)

        return true
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