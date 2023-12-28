package com.example.paymentapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.paymentapp.viewmodel.GroupViewModel
import com.example.paymentapp.model.DebtItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Debt(navController: NavHostController, viewModel: GroupViewModel, groupId: String) {
    // Fetch the group based on groupId
    val groups by viewModel.groups.collectAsState(emptyList())
    val group = groups.find { it.id == groupId }

    // Collect the debt summary if the group is not null
    val debtSummary = group?.let {
        viewModel.calculateDebtSummary(it.id).collectAsState(initial = emptyList()).value
    } ?: emptyList()

    Scaffold(
    ) { padding ->
        DebtList(debtSummary = debtSummary, modifier = Modifier.padding(padding))
    }

    // Check if the debts place correctly
    /*val mockDebtSummary = listOf(
        DebtItem("User1", "€50"),
        DebtItem("User2", "-€30")
    )

    DebtList(debtSummary = mockDebtSummary, modifier = Modifier)*/
}

@Composable
fun DebtList(debtSummary: List<DebtItem>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // If the list is empty, you can show a placeholder or a message
        if (debtSummary.isEmpty()) {
            Text("No debts recorded", style = MaterialTheme.typography.bodyLarge)
        } else {
            // Else, display the list of debts
            debtSummary.forEach { debt ->
                DebtItem(debtItem = debt)
            }
        }
    }
}

@Composable
fun DebtItem(debtItem: DebtItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = debtItem.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = debtItem.balance,
                style = MaterialTheme.typography.bodyLarge,
                color = if (debtItem.balance.startsWith("+")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}