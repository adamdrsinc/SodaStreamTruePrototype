package com.example.sodastreamprototyping

object DemoCode {
    var ingList1 : MutableList<Pair<Int, Int>> = mutableListOf(
        Pair(2, 1),
        Pair(3, 1)
    )
    var ingList2 : MutableList<Pair<Int, Int>> = mutableListOf(
        Pair(2, 1),
        Pair(3, 1)
    )
    var ingList3 : MutableList<Pair<Int, Int>> = mutableListOf(
        Pair(4, 1),
        Pair(5, 1)
    )

    var drink1 = Drink(ingList1, name = "Strawberry Creamer", quantity = 1, isCustom = false, baseDrink = 0)
    var drink2 = Drink(ingList2, name = "Something Something",quantity = 1, isCustom = false, iceQuantity = 5, baseDrink = 1)
    var drink3 = Drink(ingList3, name = "Other Thing", quantity = 1, isCustom = false, iceQuantity = 3, baseDrink = 2)

    val drinksDemoList = listOf(
        drink1,
        drink2,
        drink3
    )

    val sampleOrders = listOf(
        Order(id = 1, description = "Order #1", status = "open"),
        Order(id = 2, description = "Order #2", status = "closed"),
        Order(id = 3, description = "Order #3", status = "closed")
    )
}