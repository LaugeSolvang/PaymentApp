package com.example.paymentapp.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.paymentapp.model.Participant
import com.example.paymentapp.model.User
import com.example.paymentapp.viewmodel.GroupViewModel

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
                // Create a new participant and add it to the group
                val newUser = User("user3", participantName, "phoneNumber")
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