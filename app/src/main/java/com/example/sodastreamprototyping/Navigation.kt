package com.example.sodastreamprototyping

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun Navigation(startDestination: String, orders: List<Order>)
{
    val navController = rememberNavController();

    NavHost(navController, startDestination = startDestination) {
        composable(route = Screen.SignIn.route) {
            SignInScreen(
                onSignUpClick = { navController.navigate("sign_up") },
                onSignInSuccess = { navController.navigate("home") },
                navController = navController
            )
        }
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onSignInClick = { navController.popBackStack() }
            )
        }
        composable(route = Screen.Home.route) {
            Home(navController = navController, orders = orders)
        }

        composable(route = Screen.Basket.route){
            ShoppingBasket(navController)
        }

        composable(
            route = Screen.Edit.route + "/{drinkID}",
            arguments = listOf(
                navArgument("drinkID"){
                    type = NavType.IntType
                    nullable = false
                }
            )){
            entry ->
            EditDrinkPage(navController = navController, drinkID = entry.arguments?.getInt("drinkID"), orders = orders)
        }

//        composable(route = Screen.OrderHistory.route) {
//            OrderHistoryScreen(orders = listOf()) // Pass actual data from your ViewModel or Repository
//        }
        composable(route = Screen.OrderHistory.route) {
            OrderHistoryScreenDemo(navController)
        }

        composable(route = Screen.NewDrink.route){
            NewDrinkPage(navController , orders = orders)
        }

    }
}