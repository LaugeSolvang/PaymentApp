package com.example.paymentapp.ui.pages

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.paymentapp.model.Group
import com.example.paymentapp.viewmodel.GroupViewModel
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupManagement(viewModel: GroupViewModel, groupId: String) {
    val navController = rememberNavController()
    val tabTitles = listOf("expenses", "debt")

    // Determine the selected tab index based on the current route
    val selectedTabIndex = when (navController.currentBackStackEntryAsState().value?.destination?.route) {
        "expenses" -> 0
        "debt" -> 1
        else -> 0 // default to the first tab if the route is unknown
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = index == selectedTabIndex,
                                onClick = { navController.navigate(title) {
                                    // Avoid multiple copies of the same destination on the stack
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }},
                                text = {
                                    Text(
                                        text = title.replaceFirstChar { it.uppercase() },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            )
                        }
                    }
                },
            actions = {
                // Add participant button
                IconButton(onClick = {
                    navController.navigate("addParticipant")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Participant")
                }
                IconButton(
                    onClick = {
                        navController.navigate("removeParticipant")
                    }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Participant")
                }
            }

    )
}
    ) { innerPadding ->
        // Inside GroupManagement composable function
        NavHost(
            navController = navController,
            startDestination = "expenses",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("expenses") {
                Expenses(navController, viewModel, groupId)
            }
            composable("debt") {
                // Pass the group ID as a string instead of the whole Group object
                Debt(navController, viewModel, groupId)
            }
            composable("addExpense") {
                ExpenseAdd(navController, viewModel, groupId)
            }
            composable("addParticipant") {
                AddParticipant(navController, viewModel, groupId)
            }
            composable("removeParticipant") {
                RemoveParticipant(navController, viewModel, groupId)
            }
        }
    }
}
