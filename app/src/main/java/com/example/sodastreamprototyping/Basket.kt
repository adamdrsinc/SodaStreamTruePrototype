package com.example.sodastreamprototyping

class Basket {
    companion object{
        private val basketDrinks : ArrayList<Drink> = arrayListOf()

        fun addDrink(drink: Drink){
            var alreadyDrink = basketDrinks.find{
                it.name == drink.name
            }

            if(alreadyDrink == null){
                basketDrinks.add(drink)
            }else{
                alreadyDrink.quantity += 1
            }
        }

        fun getDrinks(): ArrayList<Drink>{
            return basketDrinks
        }
    }
}