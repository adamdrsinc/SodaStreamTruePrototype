package com.example.sodastreamprototyping

sealed class Screen(val route: String) {
    object SignIn: Screen("sign_in")
    object SignUp: Screen("sign_up")
    object Home: Screen("home")
    object Basket: Screen("basket")
    object Edit: Screen("edit")
    object NewDrink: Screen("new_drink")
    object OrderHistory: Screen("order_history")
    object Account: Screen("account")
    object Settings: Screen("settings")



    fun withArgs(vararg args: String): String{
        return buildString{
            append(route)
            args.forEach {
                arg->
                append("/$arg")
            }
        }
    }
}