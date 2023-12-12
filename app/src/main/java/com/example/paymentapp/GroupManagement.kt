package com.example.paymentapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight

@Composable
fun GroupManagement(group: Group) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Expenses", "Debt")

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> Expenses(group) // Define this composable to show expenses content
            1 -> Debt()    // Define this composable to show debt content
        }
    }
}
