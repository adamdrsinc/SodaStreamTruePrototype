package com.example.sodastreamprototyping

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practice.ApiRequestHelper
import com.example.sodastreamprototyping.viewModel.NavigationViewModel

@Composable
fun Navigation(startDestination: String)
{
    val navController = rememberNavController()
    val viewModel : NavigationViewModel = viewModel()


    fun navigateToEditDrink(drink: Drink) {
        viewModel.selectedDrink = drink
        navController.navigate(Screen.Edit.route)
    }


    NavHost(navController, startDestination = startDestination) {
        composable(route = Screen.SignIn.route) {
            SignInScreen(
                onSignUpClick = { navController.navigate("sign_up") },
                onSignInSuccess = {
                    navController.navigate("home")
                    ApiRequestHelper.retrieveAllNeededData(navController.context)
                                  },
                navController = navController
            )
        }
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onSignInClick = { navController.popBackStack() }
            )
        }
        composable(route = Screen.Home.route) {
            Home(navController = navController){navigateToEditDrink(it)}
        }

        composable(route = Screen.Basket.route){
            ShoppingBasket(navController){navigateToEditDrink(it)}
        }

        composable(route = Screen.Edit.route){
            EditDrinkPage(navController, viewModel.selectedDrink)
        }

        composable(route = Screen.OrderHistory.route) {
            OrderHistoryScreenDemo(navController)
        }

    }

}
