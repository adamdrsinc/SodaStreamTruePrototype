package com.example.sodastreamprototyping

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    navController: NavController,
    hasOpenOrders: Boolean,// Make openOrderCount observable for dynamic updates
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "John Doe",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Account") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Order History") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        UserPreferences.setLoggedIn(context, false)
                        scope.launch { drawerState.close() }
                        navController.navigate("sign_in") {
                            popUpTo("home") { inclusive = true }  // Adjust the route as necessary
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Logout")
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(80.dp),
                            )
                            Text(
                                text = "Soda Sensation",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // Order History Button with Badge
                        if (hasOpenOrders) {
                            BadgedBox(
                                badge = { Badge(containerColor = Color.Red) {} } // Empty badge with only red background
                            ) {
                                IconButton(onClick = {
                                    navController.navigate(Screen.OrderHistory.route)
                                }) {
                                    Icon(Icons.Default.History, contentDescription = "Order History")
                                }
                            }
                        } else {
                            IconButton(onClick = {
                                navController.navigate(Screen.OrderHistory.route)
                            }) {
                                Icon(Icons.Default.History, contentDescription = "Order History")
                            }
                        }
                        IconButton(onClick = {
                            navController.navigate(Screen.Basket.route)
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                )
            },
            content = content
        )
    }
}
