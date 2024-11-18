package com.example.sodastreamprototyping

class Basket {
    companion object{
        val basketDrinks : ArrayList<Drink> = arrayListOf()
        private var lastDrinkID: Int = 0

        fun saveDrink(drink: Drink){
            val oldDrink = getDrinks().indexOfFirst { it.drinkID == drink.drinkID }
            if(oldDrink == -1){
                addDrink(drink)
                return
            }
            basketDrinks[oldDrink] = drink
        }
        fun addDrink(drink: Drink){
            drink.drinkID = getNextDrinkID()
            basketDrinks.add(drink)
//            var drinkExists = basketDrinks.find{
//                it.name == drink.name
//            }
//
//            if(drinkExists == null){
//                drink.drinkID = getNextDrinkID()
//                basketDrinks.add(drink)
//            }else{
//                drinkExists.quantity++
//            }
        }


        fun getDrinks(): ArrayList<Drink>{
            return basketDrinks
        }

        fun getNextDrinkID(): Int{
            return ++lastDrinkID
        }
    }
}