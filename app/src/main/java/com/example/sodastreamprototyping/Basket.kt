package com.example.sodastreamprototyping

class Basket {
    companion object{
        val basketDrinks : ArrayList<Drink> = arrayListOf()

        fun addDrink(drink: Drink){
            var drinkExists = basketDrinks.find{
                it.name == drink.name
            }

            if(drinkExists == null){
                drink.drinkID = getNextDrinkID()
                basketDrinks.add(drink)
            }else{
                drinkExists.quantity++
            }
        }

        fun getDrinks(): ArrayList<Drink>{
            return basketDrinks
        }

        fun getNextDrinkID(): Int{
            var id = 0
            basketDrinks.forEach {
                id++
            }

            return id + 1
        }
    }
}