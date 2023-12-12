package com.example.paymentapp

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

@Composable
fun Expenses(group: Group) {
    Column {
        Text(text = "Expenses for ${group.name}", style = MaterialTheme.typography.titleSmall)
        LazyColumn {
            items(group.participants) { participant ->
                ParticipantExpenses(participant)
            }
        }
    }
}


@Composable
fun ParticipantExpenses(participant: Participant) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display participant's name and 'Paid by' text
            Text(text = "Paid by: ${participant.name}", style = MaterialTheme.typography.bodySmall)

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
