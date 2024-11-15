package com.example.sodastreamprototyping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sodastreamprototyping.viewModel.HomeViewModel

@Composable
fun Home(navController: NavController, onDrinkEdit: (Drink) -> Unit)
{
    // stores current page to come back to
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(context)
    )

    val selectedTabIndex by homeViewModel.selectedTabIndex.collectAsState(initial = 0)
    val tabs = listOf("Menu", "My Drinks", "Generate Drinks")

    MainLayout(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { homeViewModel.setSelectedTab(index) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> MenuPage()
                1 -> MyDrinksPage(navController, onDrinkEdit)
                2 -> GenerateDrinksPage(onDrinkEdit)
            }
        }
    }
}

