package com.example.paymentapp.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.paymentapp.model.Participant
import com.example.paymentapp.model.User
import com.example.paymentapp.viewmodel.GroupViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveParticipant(navController: NavHostController, viewModel: GroupViewModel, groupId: String) {
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
                // Remove the participant by calling the removeParticipant method
                viewModel.removeParticipant(groupId, participantName)
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Remove Participant")
        }
    }
}