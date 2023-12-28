package com.example.paymentapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.paymentapp.model.Group
import com.example.paymentapp.model.Participant
import com.example.paymentapp.model.User
import com.example.paymentapp.ui.pages.CreateGroup
import com.example.paymentapp.ui.pages.GroupManagement
import com.example.paymentapp.ui.pages.Groups
import com.example.paymentapp.ui.pages.SecondScreen
import com.example.paymentapp.ui.theme.PaymentAppTheme
import com.example.paymentapp.viewmodel.GroupViewModel
import androidx.compose.runtime.collectAsState


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
                        composable("createGroup") {
                            CreateGroup(navController, viewModel)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddParticipant(navController: NavHostController, viewModel: GroupViewModel, groupId: String) {
    var participantName by remember { mutableStateOf("") }

    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
    ) {
        TextField(
                value = participantName,
                onValueChange = { participantName = it },
                label = { Text("Participant Name") },
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
        )

        Button(
                onClick = {
                    // Create a new participant and navigate back to the group management screen
                    val newUser = User("","","")
                    val newParticipant = Participant(user = newUser, expenses = emptyList())
                    viewModel.addParticipant(groupId, newParticipant)
                    navController.popBackStack()
                },
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
        ) {
            Text("Add Participant")
        }
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RemoveParticipant(navController: NavHostController, viewModel: GroupViewModel, groupId: String) {
    // Observe changes in the groups LiveData directly
    val groups by rememberUpdatedState(newValue = viewModel.groups.value)

    // Find the group with the specified ID
    val group = groups.find { it.id == groupId }

    // Extract participants from the group
    val participants = group?.participants ?: emptyList()

    LazyColumn {
        items(participants) { participant ->
            Text(
                    text = participant.user.name,
                    modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Remove the participant and navigate back to the group management screen
                                viewModel.removeParticipant(groupId, participant.user.id)
                                navController.popBackStack()
                            }
                            .padding(8.dp)
            )
        }
    }
}
