package com.example.paymentapp.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.paymentapp.model.Group
import com.example.paymentapp.viewmodel.GroupViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroup(navController: NavHostController, viewModel: GroupViewModel) {
    var groupName by remember { mutableStateOf(TextFieldValue()) }

    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
    ) {
        TextField(
                value = groupName.text,
                onValueChange = { groupName = TextFieldValue(it)},
                label = { Text("Group Name") },
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
        )

        Button(
                onClick = {
                    // Create a new group and navigate back to the groups list
                    val newGroup = Group(
                            id = UUID.randomUUID().toString(),
                            name = groupName.text,
                            balance = "0", // You can set an initial balance if needed
                            participants = emptyList() // Initialize with an empty list
                    )
                    viewModel.createGroup(newGroup)
                    navController.popBackStack()
                },
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
        ) {
            Text("Create Group")
        }
    }
}