package com.example.paymentapp.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.example.paymentapp.model.Group
import com.example.paymentapp.viewmodel.GroupViewModel

@Composable
fun Debt(navController: NavHostController, viewModel: GroupViewModel, groupId: String) {
    val groups by viewModel.groups.collectAsState(emptyList())
    val group = groups.find { it.id == groupId }

    Text("Debt Content")
}
