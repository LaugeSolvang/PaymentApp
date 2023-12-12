package com.example.paymentapp.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.paymentapp.model.Participant
import com.example.paymentapp.viewmodel.GroupViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Expenses(groupId: String, navController: NavHostController) {
    val viewModel: GroupViewModel = viewModel()
    val groups by viewModel.groups
    val group = groups.find { it.id == groupId }

    Log.d("ExpensesLog", "Expenses composable recomposed for groupId: $groupId")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addExpense/$groupId") }) {
                Text("Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        group?.let {
            LazyColumn(contentPadding = innerPadding) {
                items(group.participants) { participant ->
                    if (participant.expenses.isNotEmpty()) {
                        ParticipantExpenses(participant)
                    }
                }
            }
        }
    }
}
@Composable
fun ParticipantExpenses(participant: Participant) {
    Log.d("ExpensesLog", "ParticipantExpenses composable recomposed for participant: ${participant.expenses}")
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display participant's user name and 'Paid by' text
            Text(text = "Paid by: ${participant.user.name}", style = MaterialTheme.typography.bodySmall)

            // List expenses
            participant.expenses.forEach { expense ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Display each expense description
                    Text(text = expense.description, style = MaterialTheme.typography.bodyLarge)

                    // Display expense amount on the right
                    Text(text = "${expense.amount}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}