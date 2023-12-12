package com.example.paymentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.paymentapp.ui.theme.PaymentAppTheme

import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaymentAppTheme {
                val navController = rememberNavController()
                val currentRoute = getCurrentRoute(navController)
                Scaffold(
                    topBar = {
                        val title = when (currentRoute) {
                            "first" -> "CashApp"
                            "groupManagement" -> "Group Management"
                            // Add other routes and their titles here
                            else -> "CashApp"
                        }
                        AppBar(navController, currentRoute, title)
                    },
                    // Your Scaffold content...
                ) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = "first",
                        modifier = Modifier.padding(innerPadding) // Use the innerPadding here
                    ) {
                        composable("first") { FirstScreen(navController) }
                        composable("second") { SecondScreen(navController) }
                        composable("groupManagement/{groupId}") { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId")
                            val viewModel: GroupViewModel = viewModel() // Explicitly specify the ViewModel type
                            val group = viewModel.getGroupById(groupId)
                            if (group != null) {
                                GroupManagement(group)
                            }
                        }
                        composable("expenses/{groupId}") { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getString("groupId")
                            val viewModel: GroupViewModel = viewModel() // Explicitly specify the ViewModel type
                            val group = viewModel.getGroupById(groupId)
                            if (group != null) {
                                Expenses(group)
                            }
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavHostController, currentRoute: String?, title: String) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (currentRoute != "first") {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                }
            }
        }
    )
}

@Composable
fun getCurrentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}



