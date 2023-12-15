package com.example.paymentapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.paymentapp.model.Group
import com.example.paymentapp.ui.pages.GroupManagement
import com.example.paymentapp.ui.pages.Groups
import com.example.paymentapp.ui.pages.SecondScreen
import com.example.paymentapp.ui.theme.PaymentAppTheme
import com.example.paymentapp.viewmodel.GroupViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaymentAppTheme {
                val navController = rememberNavController()
                val currentRoute = getCurrentRoute(navController)
                var currentGroupId by remember { mutableStateOf<String?>(null) }
                val viewModel: GroupViewModel = viewModel()

                Scaffold(
                    topBar = {
                        val group = currentGroupId?.let { groupId ->
                            viewModel.getGroupById(groupId)
                        }
                        AppBar(navController, currentRoute, "CashApp", group)
                    },
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "first",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("first") { Groups(navController, viewModel) }
                        composable("second") { SecondScreen(navController) }
                        composable("groupManagement/{groupId}") { backStackEntry ->
                            currentGroupId = backStackEntry.arguments?.getString("groupId")
                            val group = currentGroupId?.let { groupId ->
                                viewModel.getGroupById(groupId)
                            }
                            if (group != null) {
                                GroupManagement(viewModel, group)
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
fun AppBar(navController: NavHostController, currentRoute: String?, title: String, group: Group? = null) {
    CenterAlignedTopAppBar(
        title = {
            if (currentRoute == "groupManagement/{groupId}" && group != null) {
                Column {
                    Text(group.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = group.participants.joinToString(", ") { it.user.name },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Text(title)
            }
        },
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
